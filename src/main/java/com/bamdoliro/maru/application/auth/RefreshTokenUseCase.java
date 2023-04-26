package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.domain.Token;
import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class RefreshTokenUseCase {

    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public TokenResponse execute(String refreshToken) {
        Token token = tokenRepository.findByToken(refreshToken)
                .orElseThrow(ExpiredTokenException::new);

        return TokenResponse.builder()
                .accessToken(tokenService.generateAccessToken(token.getEmail()))
                .build();
    }
}
