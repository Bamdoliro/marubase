package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.auth.domain.Token;

public class AuthFixture {

    public static String createAccessTokenString() {
        return "new.access.token";
    }

    public static String createRefreshTokenString() {
        return "new.refresh.token";
    }

    public static Token createAccessToken() {
        return Token.builder()
                .email("maru@bamdoliro.com")
                .token("new.access.token")
                .build();
    }

    public static Token createRefreshToken() {
        return Token.builder()
                .email("maru@bamdoliro.com")
                .token("new.refresh.token")
                .build();
    }

    public static String createAuthHeader() {
        return "Bearer thisis.access.token";
    }
}
