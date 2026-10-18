package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.domain.type.TokenType;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@UseCase
public class RefreshTokenUseCase {

    private final TokenService tokenService;
    private final AuthSessionService authSessionService;

    public TokenResponse execute(String refreshToken) {
        if (tokenService.getType(refreshToken) != TokenType.REFRESH_TOKEN) {
            throw new InvalidTokenException();
        }
        String uuid = tokenService.getUuid(refreshToken);
        String sessionId = tokenService.getSessionId(refreshToken);
        authSessionService.validate(UUID.fromString(uuid), sessionId);

        return TokenResponse.builder()
                .accessToken(tokenService.generateAccessToken(uuid, sessionId))
                .refreshToken(refreshToken)
                .build();
    }

}
