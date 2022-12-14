package com.example.springbootoauth2.oauth.info.impl;

import com.example.springbootoauth2.oauth.info.OAuth2UserInfo;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) return null;

        return String.valueOf(response.get("id"));
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) return null;

        return String.valueOf(response.get("nickname"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) return null;

        return String.valueOf(response.get("email"));
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) return null;

        return String.valueOf(response.get("profile_image"));
    }
}
