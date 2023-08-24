package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.auth.TokenRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class LogOutUseCase {

    private final TokenRepository tokenRepository;

    public void execute(User user) {
        tokenRepository.deleteById(user.getPhoneNumber());
    }
}
