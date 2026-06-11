package com.bamdoliro.maru.shared.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private final String ACCESS_TOKEN  = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";
    private static final int ACCESS_MAX_AGE = 3600;
    private static final int REFRESH_MAX_AGE = 1296000;

    @Value("${domain.name}")
    private String DOMAIN;

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addCookie(response, buildCookie(ACCESS_TOKEN, accessToken, ACCESS_MAX_AGE));
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addCookie(response, buildCookie(REFRESH_TOKEN, refreshToken, REFRESH_MAX_AGE));
    }

    public void deleteAccessTokenCookie(HttpServletResponse response) {
        addCookie(response, buildCookie(ACCESS_TOKEN, "", 0));
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        addCookie(response, buildCookie(REFRESH_TOKEN, "", 0));
    }

    private void addCookie(HttpServletResponse response, ResponseCookie cookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private ResponseCookie buildCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .domain(DOMAIN)
                .maxAge(maxAge)
                .build();
    }

}