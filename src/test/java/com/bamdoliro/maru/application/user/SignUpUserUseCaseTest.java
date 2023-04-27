package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.VerificationCodeMismatchException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.EmailVerificationRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignUpUserUseCaseTest {

    @InjectMocks
    private SignUpUserUseCase signUpUserUseCase;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void 유저를_생성한다() {
        // given
        User user = UserFixture.createUser();
        EmailVerification verification = UserFixture.createVerification();
        SignUpUserRequest request = new SignUpUserRequest(user.getEmail(), verification.getCode(), "비밀번호");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        given(emailVerificationRepository.findById(request.getEmail())).willReturn(Optional.of(verification));
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        signUpUserUseCase.execute(request);

        // then
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void 이메일_인증을_요청하지_않았거나_만료되었다면_에러가_발생한다() {
        // given
        SignUpUserRequest request = new SignUpUserRequest("이메일", "ABC123", "비밀번호");

        given(emailVerificationRepository.findById(request.getEmail())).willReturn(Optional.empty());

        // when and then
        assertThrows(VerifyingHasFailedException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void 이메일_인증_코드가_틀렸다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        EmailVerification verification = UserFixture.createVerification();
        SignUpUserRequest request = new SignUpUserRequest(user.getEmail(), "다른코드같을수가없는코드", "비밀번호");

        given(emailVerificationRepository.findById(request.getEmail())).willReturn(Optional.of(verification));

        // when and then
        assertThrows(VerificationCodeMismatchException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void 이미_유저가_있다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        EmailVerification verification = UserFixture.createVerification();
        SignUpUserRequest request = new SignUpUserRequest(user.getEmail(), verification.getCode(), "비밀번호");

        given(emailVerificationRepository.findById(request.getEmail())).willReturn(Optional.of(verification));
        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when and then
        assertThrows(UserAlreadyExistsException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any());
    }
}