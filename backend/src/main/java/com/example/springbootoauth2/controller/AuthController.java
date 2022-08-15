package com.example.springbootoauth2.controller;

import com.example.springbootoauth2.config.AppProperties;
import com.example.springbootoauth2.model.dto.AuthReq;
import com.example.springbootoauth2.model.entity.UserRefreshToken;
import com.example.springbootoauth2.oauth.entity.RoleType;
import com.example.springbootoauth2.oauth.entity.UserPrincipal;
import com.example.springbootoauth2.oauth.token.AuthToken;
import com.example.springbootoauth2.oauth.token.AuthTokenProvider;
import com.example.springbootoauth2.repository.UserRefreshTokenRepository;
import com.example.springbootoauth2.utils.CookieUtil;
import com.example.springbootoauth2.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final long THREE_DAYS_MSEC = 259200000;
    private static final String REFRESH_TOKEN = "refresh_token";

    private final AppProperties appProperties;
    private final AuthTokenProvider authTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public AuthController(AppProperties appProperties, AuthTokenProvider authTokenProvider, AuthenticationManager authenticationManager,
                          UserRefreshTokenRepository userRefreshTokenRepository) {
        this.appProperties = appProperties;
        this.authTokenProvider = authTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthReq authReq, HttpServletRequest request,
                                                     HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReq.getId(),
                        authReq.getPassword()
                )
        );

        String userId = authReq.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        String role = ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode();
        Date tokenExpiry = new Date(now.getTime() + appProperties.getAuth().getTokenExpiry());
        AuthToken accessToken = authTokenProvider.createAuthToken(userId, role, tokenExpiry);

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = authTokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userId).orElse(null);
        if (userRefreshToken == null) {
            // 없는 경우 새로 등록
            userRefreshToken = new UserRefreshToken(userId, refreshToken.getToken());
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        } else {
            // DB에 refresh 토큰 업데이트
            userRefreshToken.updateRefreshToken(refreshToken.getToken());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", accessToken.getToken());

        return ok(responseMap);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> responseMap = new HashMap<>();
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);
        if (!authToken.validate()) {
            responseMap.put("message", "Invalid Access Token");
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            responseMap.put("message", "Token Is Not Expired Yet");
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = authTokenProvider.convertAuthToken(refreshToken);

        if (authRefreshToken.validate()) {
            responseMap.put("message", "Invalid Refresh Token");
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken)
                .orElse(null);
        if (userRefreshToken == null) {
            responseMap.put("message", "Invalid Refresh Token");
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }

        Date now = new Date();
        AuthToken newAccessToken = authTokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = authTokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            userRefreshToken.updateRefreshToken(authRefreshToken.getToken());

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        responseMap.put("token", newAccessToken.getToken());

        return ok(responseMap);
    }
}
