package com.example.springbootoauth2.config.security;

import com.example.springbootoauth2.config.AppProperties;
import com.example.springbootoauth2.oauth.exception.RestAuthenticationEntryPoint;
import com.example.springbootoauth2.oauth.filter.TokenAuthenticationFilter;
import com.example.springbootoauth2.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.example.springbootoauth2.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.example.springbootoauth2.oauth.handler.TokenAccessDeniedHandler;
import com.example.springbootoauth2.oauth.repository.OAuth2AuthorizationRequestRepository;
import com.example.springbootoauth2.oauth.service.CustomOAuth2UserService;
import com.example.springbootoauth2.oauth.token.AuthTokenProvider;
import com.example.springbootoauth2.repository.UserRefreshTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String[] PATHS = {"/auth/**", "/oauth2/**", "/h2-console/**"};

	private final AppProperties appProperties;
	private final AuthTokenProvider authTokenProvider;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final UserRefreshTokenRepository userRefreshTokenRepository;
	private final HandlerExceptionResolver handlerExceptionResolver;
	private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

	public SecurityConfig(AppProperties appProperties, AuthTokenProvider authTokenProvider,
						  CustomOAuth2UserService customOAuth2UserService, UserRefreshTokenRepository userRefreshTokenRepository,
						  HandlerExceptionResolver handlerExceptionResolver, OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository) {
		this.appProperties = appProperties;
		this.authTokenProvider = authTokenProvider;
		this.customOAuth2UserService = customOAuth2UserService;
		this.userRefreshTokenRepository = userRefreshTokenRepository;
		this.handlerExceptionResolver = handlerExceptionResolver;
		this.oAuth2AuthorizationRequestRepository = oAuth2AuthorizationRequestRepository;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors().configurationSource(corsConfigurationSource())
				.and()
				.csrf().disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.formLogin().disable()
				.httpBasic().disable()
				.exceptionHandling()
					.authenticationEntryPoint(new RestAuthenticationEntryPoint())
				.accessDeniedHandler(tokenAccessDeniedHandler())
				.and()
				.authorizeRequests(authRequest -> {
					authRequest.antMatchers(PATHS).permitAll();
					authRequest.anyRequest().authenticated();
				})
				.oauth2Login()
				.authorizationEndpoint().baseUri("/oauth2/authorize")
				.authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
				.and()
				.redirectionEndpoint().baseUri("/oauth2/code/*")
				.and()
				.userInfoEndpoint().userService(customOAuth2UserService)
				.and()
				.successHandler(oAuth2AuthenticationSuccessHandler())
				.failureHandler(oAuth2AuthenticationFailureHandler())
		;

		http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().antMatchers("/h2-console/**");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(authTokenProvider);
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler(authTokenProvider, appProperties, userRefreshTokenRepository,
				oAuth2AuthorizationRequestRepository);
	}

	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestRepository);
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public TokenAccessDeniedHandler tokenAccessDeniedHandler() {
		return new TokenAccessDeniedHandler(handlerExceptionResolver);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOriginPattern("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
