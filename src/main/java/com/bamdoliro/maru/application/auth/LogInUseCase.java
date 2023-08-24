package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class LogInUseCase {

    private final TokenService tokenService;
    private final UserFacade userFacade;

    public TokenResponse execute(LogInRequest request) {
        User user = userFacade.getUser(request.getPhoneNumber());
        if (!user.getPassword().match(request.getPassword())) {
            throw new PasswordMismatchException();
        }

        return TokenResponse.builder()
                .accessToken(tokenService.generateAccessToken(user.getPhoneNumber()))
                .refreshToken(tokenService.generateRefreshToken(user.getPhoneNumber()))
                .build();
    }
}
