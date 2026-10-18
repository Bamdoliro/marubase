package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class SignUpUserUseCase {

    private final VerificationProofService verificationProofService;
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
        verificationProofService.consume(request.getPhoneNumber(),
                VerificationType.SIGNUP, request.getVerificationToken());

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException();
        }
    }
}
