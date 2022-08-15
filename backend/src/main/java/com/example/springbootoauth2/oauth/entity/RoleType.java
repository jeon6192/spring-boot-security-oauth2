package com.example.springbootoauth2.oauth.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자"),
    GUEST("GUEST", "게스트");

    private final String code;
    private final String displayName;

    RoleType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static RoleType of(String code) {
        return Arrays.stream(RoleType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
