package com.example.springbootoauth2.model.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthReq {
    private String id;
    private String password;

    @Builder
    public AuthReq(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
