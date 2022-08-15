package com.example.springbootoauth2.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Auth auth = new Auth();

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Auth {
        @Value("${token-secret}")
        private String tokenSecret;
        @Value("${token-expiry}")
        private long tokenExpiry;
        @Value("${refresh-token-expiry}")
        private long refreshTokenExpiry;
        @Value("${authorized-redirect-uri}")
        private String authorizedRedirectUri;

        @Builder
        public Auth(String tokenSecret, long tokenExpiry, long refreshTokenExpiry, String authorizedRedirectUri) {
            this.tokenSecret = tokenSecret;
            this.tokenExpiry = tokenExpiry;
            this.refreshTokenExpiry = refreshTokenExpiry;
            this.authorizedRedirectUri = authorizedRedirectUri;
        }
    }
}
