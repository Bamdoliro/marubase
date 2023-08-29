package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

class VerifyUseCaseTest {

    @InjectMocks
    private VerifyUseCase verifyUseCase;

    @Mock
    private VerificationRepository verificationRepository;

    @Test
    void 인증을_완료한다() {
        // given
        Verification verification = UserFixture.createVerification(false);
        VerifyRequest request = new VerifyRequest(verification.getPhoneNumber(), verification.getCode());
        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(verification));

        // when
        verifyUseCase.execute(request);

        // then
        verify(verificationRepository, times(1)).updateVerification(verification.getPhoneNumber(), true);
    }

    @Test
    void 인증할_때_인증_요청을_하지_않았거나_만료되었다면_에러가_발생한다() {
        // given
        VerifyRequest request = new VerifyRequest("01085852525", "123456");
        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.empty());

        // when and then
        assertThrows(VerifyingHasFailedException.class, () -> verifyUseCase.execute(request));
        verify(verificationRepository, times(1)).findById(request.getPhoneNumber());
        verify(verificationRepository, never()).updateVerification(request.getPhoneNumber(), true);
    }

    @Test
    void 인증할_때_코드가_틀렸다면_에러가_발생한다() {
        // given
        Verification verification = UserFixture.createVerification(false);
        VerifyRequest request = new VerifyRequest(verification.getPhoneNumber(), "아무래도다를수밖에없는코드");
        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(verification));

        // when and then
        assertThrows(VerificationCodeMismatchException.class, () -> verifyUseCase.execute(request));
        verify(verificationRepository, times(1)).findById(request.getPhoneNumber());
        verify(verificationRepository, never()).updateVerification(request.getPhoneNumber(), true);
    }
}