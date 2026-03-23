package com.bamdoliro.maru.shared.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN  = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addCookie(response, ResponseCookie.from(ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(3600)
                .build());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addCookie(response, ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(1296000)
                .build());
    }

    public void deleteAccessTokenCookie(HttpServletResponse response) {
        addCookie(response, ResponseCookie.from(ACCESS_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        addCookie(response, ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build());
    }

    private void addCookie(HttpServletResponse response, ResponseCookie cookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}