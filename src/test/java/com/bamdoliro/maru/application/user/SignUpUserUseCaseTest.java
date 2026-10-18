package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SignUpUserUseCaseTest {

    private final VerificationProofService proofs = mock(VerificationProofService.class);
    private final UserRepository users = mock(UserRepository.class);
    private final SignUpUserUseCase useCase = new SignUpUserUseCase(proofs, users);
    private final SignUpUserRequest request = new SignUpUserRequest("01000000000", "테스트", "password123!", "proof");

    @Test
    void 회원가입_증명을_소비한_뒤_유저를_저장한다() {
        useCase.execute(request);
        var order = inOrder(proofs, users);
        order.verify(proofs).consume(request.getPhoneNumber(), VerificationType.SIGNUP, "proof");
        order.verify(users).existsByPhoneNumber(request.getPhoneNumber());
        order.verify(users).save(any(User.class));
    }

    @Test
    void 인증_증명이_유효하지_않으면_저장하지_않는다() {
        doThrow(new VerifyingHasFailedException()).when(proofs)
                .consume(request.getPhoneNumber(), VerificationType.SIGNUP, "proof");
        assertThrows(VerifyingHasFailedException.class, () -> useCase.execute(request));
        verifyNoInteractions(users);
    }

    @Test
    void 이미_유저가_있으면_저장하지_않는다() {
        when(users.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> useCase.execute(request));
        verify(users, never()).save(any());
    }
}
