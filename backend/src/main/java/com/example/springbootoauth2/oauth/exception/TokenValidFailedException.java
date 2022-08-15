package com.example.springbootoauth2.oauth.exception;

public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException() {
        super("Failure Generate Token");
    }

    private TokenValidFailedException(String message) {
        super(message);
    }
}
