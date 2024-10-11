package com.bamdoliro.maru.domain.auth.service;

import com.bamdoliro.maru.domain.auth.domain.Token;
import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;
    private final UserFacade userFacade;

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
        Claims claims = Jwts.claims();
        claims.put("uuid", uuid);
        claims.put("type", type.name());
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + time))
                .signWith(getSigningKey(jwtProperties.getSecretKey()), SignatureAlgorithm.HS256)
                .compact();
    }

    public User getUser(String token) {
        return userFacade.getUser(getUuid(token));
    }

    public String getUuid(String uuid) {
        return extractAllClaims(uuid).get("uuid", String.class);
    }

    public String getType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    private Claims extractAllClaims(String token) {
        try {
            validateSignatureAlgorithm(token);

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(jwtProperties.getSecretKey()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    private void validateSignatureAlgorithm(String token) throws JsonProcessingException {
        String[] tokenParts = token.split("\\.");
        String headerJson = new String(Base64.getDecoder().decode(tokenParts[0]));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> headerMap = objectMapper.readValue(headerJson, new TypeReference<>() {});
        String algorithm = (String) headerMap.get("alg");

        if (!algorithm.equals(SignatureAlgorithm.HS256.getValue())) {
            throw new InvalidTokenException();
        }
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
