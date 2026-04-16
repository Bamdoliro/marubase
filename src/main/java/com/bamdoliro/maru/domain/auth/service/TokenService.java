package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.domain.Token;
import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
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

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;

    public String generateAccessToken(String uuid) {
        return generateToken(uuid, TokenType.ACCESS_TOKEN, jwtProperties.getAccessExpirationTime());
    }

    public String generateRefreshToken(String uuid) {
        String token = generateToken(uuid, TokenType.REFRESH_TOKEN, jwtProperties.getRefreshExpirationTime());
        tokenRepository.save(
                Token.builder()
                        .uuid(uuid)
                        .token(token)
                        .build());

        return token;
    }

    private String generateToken(String uuid, TokenType type, Long time) {
        Date now = new Date();

        return Jwts.builder()
                .claim("uuid", uuid)
                .claim("type", type.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + time))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUuid(String token) {
        return extractAllClaims(token).get("uuid", String.class);
    }

    public TokenType getType(String token) {
        return TokenType.valueOf(
                extractAllClaims(token).get("type", String.class)
        );
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
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