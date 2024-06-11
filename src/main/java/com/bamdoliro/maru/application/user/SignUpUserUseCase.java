package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class SignUpUserUseCase {

    private final SignUpVerificationRepository signUpVerificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(SignUpUserRequest request) {
        validate(request);

        userRepository.save(
                User.builder()
                        .phoneNumber(request.getPhoneNumber())
                        .name(request.getName())
                        .password(request.getPassword())
                        .authority(Authority.USER)
                        .build()
        );
    }

    private void validate(SignUpUserRequest request) {
        SignUpVerification signUpVerification = signUpVerificationRepository.findById(request.getPhoneNumber())
                .orElseThrow(VerifyingHasFailedException::new);

        if (!signUpVerification.getIsVerified()) {
            throw new VerifyingHasFailedException();
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException();
        }

        signUpVerificationRepository.delete(signUpVerification);
    }
}
