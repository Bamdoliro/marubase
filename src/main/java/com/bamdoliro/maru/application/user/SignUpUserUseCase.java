package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class SignUpUserUseCase {

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(SignUpUserRequest request) {
        validate(request);

        userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .build()
        );
    }

    private void validate(SignUpUserRequest request) {
        EmailVerification verification = emailVerificationRepository.findById(request.getEmail())
                .orElseThrow(VerifyingHasFailedException::new);

        if (!verification.getCode().equals(request.getCode())) {
            throw new VerificationCodeMismatchException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }
    }
}
