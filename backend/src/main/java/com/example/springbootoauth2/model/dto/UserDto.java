package com.example.springbootoauth2.model.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserDto {
    private Long idx;

    private String userId;

    private String username;

    private String password;

    private String email;

    private Boolean isVerifiedEmail;

    private String profileImageUrl;

    @Builder
    public UserDto(Long idx, String userId, String username, String password, String email, Boolean isVerifiedEmail, String profileImageUrl) {
        this.idx = idx;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isVerifiedEmail = isVerifiedEmail;
        this.profileImageUrl = profileImageUrl;
    }
}
