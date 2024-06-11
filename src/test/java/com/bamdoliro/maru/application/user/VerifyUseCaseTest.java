package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
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
    private SignUpVerificationRepository signUpVerificationRepository;

    @Test
    void 회원가입_인증을_완료한다() {
        // given
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(false);
        VerifyRequest request = new VerifyRequest(signUpVerification.getPhoneNumber(), signUpVerification.getCode(), VerificationType.SIGNUP);
        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(signUpVerification));

        // when
        verifyUseCase.execute(request);

        // then
        verify(signUpVerificationRepository, times(1)).updateSignUpVerification(signUpVerification.getPhoneNumber(), true);
    }

    @Test
    void 회원가입_인증할_때_인증_요청을_하지_않았거나_만료되었다면_에러가_발생한다() {
        // given
        VerifyRequest request = new VerifyRequest("01085852525", "123456", VerificationType.SIGNUP);
        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.empty());

        // when and then
        assertThrows(VerifyingHasFailedException.class, () -> verifyUseCase.execute(request));
        verify(signUpVerificationRepository, times(1)).findById(request.getPhoneNumber());
        verify(signUpVerificationRepository, never()).updateSignUpVerification(request.getPhoneNumber(), true);
    }

    @Test
    void 회원가입_인증할_때_코드가_틀렸다면_에러가_발생한다() {
        // given
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(false);
        VerifyRequest request = new VerifyRequest(signUpVerification.getPhoneNumber(), "아무래도다를수밖에없는코드", VerificationType.SIGNUP);
        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(signUpVerification));

        // when and then
        assertThrows(VerificationCodeMismatchException.class, () -> verifyUseCase.execute(request));
        verify(signUpVerificationRepository, times(1)).findById(request.getPhoneNumber());
        verify(signUpVerificationRepository, never()).updateSignUpVerification(request.getPhoneNumber(), true);
    }
}