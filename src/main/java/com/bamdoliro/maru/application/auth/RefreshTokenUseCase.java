package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.domain.Token;
import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@UseCase
public class RefreshTokenUseCase {

    private final TokenService tokenService;
    private final TokenRepository tokenRepository;

    @Transactional(readOnly = true)
    public TokenResponse execute(String refreshToken) {
        validate(refreshToken);
        Token token = getToken(refreshToken);

        return TokenResponse.builder()
                .accessToken(tokenService.generateAccessToken(token.getUuid()))
                .build();
    }

    private void validate(String token) {
        if (!Objects.equals(tokenService.getType(token), TokenType.REFRESH_TOKEN.name())) {
            throw new InvalidTokenException();
        }
    }

    private Token getToken(String refreshToken) {
        String uuid = tokenService.getUuid(refreshToken);
        Token token = tokenRepository.findById(uuid)
                .orElseThrow(ExpiredTokenException::new);

        validate(refreshToken, token.getToken());
        return token;
    }

    private void validate(String expectedToken, String actualToken) {
        if (!Objects.equals(expectedToken, actualToken)) {
            throw new ExpiredTokenException();
        }
    }
}
