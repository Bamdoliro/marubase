package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.value.Password;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.domain.auth.exception.WrongLoginException;
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
        User user;
        try {
            user = userFacade.getUser(request.getPhoneNumber());
            validatePassword(request.getPassword(), user.getPassword());
        } catch (UserNotFoundException | PasswordMismatchException e) {
            throw new WrongLoginException();
        }

        return TokenResponse.builder()
                .accessToken(tokenService.generateAccessToken(user.getPhoneNumber()))
                .refreshToken(tokenService.generateRefreshToken(user.getPhoneNumber()))
                .build();
    }

    private void validatePassword(String actual, Password expected) {
        if (!expected.match(actual)) {
            throw new PasswordMismatchException();
        }
    }
}
