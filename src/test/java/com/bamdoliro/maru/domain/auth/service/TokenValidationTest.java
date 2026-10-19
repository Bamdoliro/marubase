package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenValidationTest {

    private static final String SECRET = "local-test-signing-key-with-at-least-sixty-four-characters-for-hmac-sha";
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = mock(JwtProperties.class);
        when(properties.getSecretKey()).thenReturn(SECRET);
        tokenService = new TokenService(properties);
    }

    @Test
    void 정상_토큰의_UUID와_타입을_검증한다() {
        String uuid = UUID.randomUUID().toString();
        String token = token(uuid, "ACCESS_TOKEN", new Date(System.currentTimeMillis() + 60000));
        assertEquals(uuid, tokenService.getUuid(token));
        assertEquals(TokenType.ACCESS_TOKEN, tokenService.getType(token));
    }

    @Test
    void 필수_클레임이_없거나_잘못되면_거부한다() {
        Date expiration = new Date(System.currentTimeMillis() + 60000);
        assertThrows(InvalidTokenException.class, () -> tokenService.getUuid(token(null, "ACCESS_TOKEN", expiration)));
        assertThrows(InvalidTokenException.class, () -> tokenService.getUuid(token("01000000000", "ACCESS_TOKEN", expiration)));
        assertThrows(InvalidTokenException.class, () -> tokenService.getType(token(UUID.randomUUID().toString(), null, expiration)));
        assertThrows(InvalidTokenException.class, () -> tokenService.getType(token(UUID.randomUUID().toString(), "UNKNOWN", expiration)));
        assertThrows(InvalidTokenException.class, () -> tokenService.getUuid(token(UUID.randomUUID().toString(), "ACCESS_TOKEN", null)));
    }

    @Test
    void 만료되거나_변조된_토큰을_거부한다() {
        assertThrows(ExpiredTokenException.class, () -> tokenService.getUuid(
                token(UUID.randomUUID().toString(), "ACCESS_TOKEN", new Date(System.currentTimeMillis() - 60000))));
        assertThrows(InvalidTokenException.class, () -> tokenService.getUuid("invalid.token"));
    }

    private String token(String uuid, String type, Date expiration) {
        return Jwts.builder().claim("uuid", uuid).claim("type", type).claim("sid", UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis() - 120000)).expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))).compact();
    }
}
