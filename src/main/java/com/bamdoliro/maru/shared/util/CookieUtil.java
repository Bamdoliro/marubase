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

    @Value("${domain.name}")
    private String DOMAIN;

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addCookie(response, buildCookie(ACCESS_TOKEN, accessToken, 3600));
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addCookie(response, buildCookie(REFRESH_TOKEN, refreshToken, 1296000));
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