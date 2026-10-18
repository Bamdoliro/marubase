package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(String uuid, String sessionId) {
        return generateToken(uuid, sessionId, TokenType.ACCESS_TOKEN, jwtProperties.getAccessExpirationTime());
    }

    public String generateRefreshToken(String uuid, String sessionId) {
        return generateToken(uuid, sessionId, TokenType.REFRESH_TOKEN, jwtProperties.getRefreshExpirationTime());
    }

    private String generateToken(String uuid, String sessionId, TokenType type, Long time) {
        Date now = new Date();

        return Jwts.builder()
                .claim("uuid", uuid)
                .claim("sid", sessionId)
                .claim("type", type.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + time))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUuid(String token) {
        return extractAllClaims(token).get("uuid", String.class);
    }

    public String getSessionId(String token) {
        return extractAllClaims(token).get("sid", String.class);
    }

    public TokenType getType(String token) {
        return TokenType.valueOf(
                extractAllClaims(token).get("type", String.class)
        );
    }

    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String uuid = claims.get("uuid", String.class);
            if (uuid == null || !UUID.fromString(uuid).toString().equals(uuid)
                    || claims.getIssuedAt() == null || claims.getExpiration() == null
                    || !claims.getExpiration().after(claims.getIssuedAt())) {
                throw new InvalidTokenException();
            }
            TokenType.valueOf(claims.get("type", String.class));
            String sessionId = claims.get("sid", String.class);
            if (sessionId == null || !UUID.fromString(sessionId).toString().equals(sessionId)) {
                throw new InvalidTokenException();
            }
            return claims;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
