package com.example.springbootoauth2.model.entity;

import com.example.springbootoauth2.oauth.entity.ProviderType;
import com.example.springbootoauth2.oauth.entity.RoleType;
import lombok.*;

import javax.persistence.*;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String userId;

    private String username;

    private String password;

    private String email;

    private Boolean isVerifiedEmail;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Builder
    public User(Long idx, String userId, String username, String password, String email, Boolean isVerifiedEmail,
                String profileImageUrl, ProviderType providerType, RoleType roleType) {
        this.idx = idx;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.isVerifiedEmail = isVerifiedEmail;
        this.profileImageUrl = profileImageUrl;
        this.providerType = providerType;
        this.roleType = roleType;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
