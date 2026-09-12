package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.exception.SignUpTemporarilyDisabledException;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.SignUpVerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private SignUpVerificationRepository signUpVerificationRepository;

    @Mock
    private UserRepository userRepository;

    // TODO: 회원가입 임시 차단을 재개하면 @Disabled 제거
    @Disabled("회원가입 임시 차단이 해제되어 있어 이 테스트는 통과하지 않음")
    @Test
    void 회원가입이_임시로_차단되어_있다() {
        // given
        SignUpUserRequest request = new SignUpUserRequest("전화번호", "김밤돌", "비밀번호");

        // when and then
        assertThrows(SignUpTemporarilyDisabledException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, never()).existsByPhoneNumber(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void 유저를_생성한다() {
        // given
        User user = UserFixture.createUser();
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(true);
        SignUpUserRequest request = new SignUpUserRequest(user.getPhoneNumber(), user.getName(), "비밀번호");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(signUpVerification));
        given(userRepository.existsByPhoneNumber(request.getPhoneNumber())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        signUpUserUseCase.execute(request);

        // then
        verify(userRepository, times(1)).existsByPhoneNumber(request.getPhoneNumber());
        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals(user.getPhoneNumber(), savedUser.getPhoneNumber());
    }

    @Test
    void 전화번호_인증을_요청하지_않았거나_만료되었다면_에러가_발생한다() {
        // given
        SignUpUserRequest request = new SignUpUserRequest("전화번호", "김밤돌", "비밀번호");

        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.empty());

        // when and then
        assertThrows(VerifyingHasFailedException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, never()).existsByPhoneNumber(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void 전화번호_인증을_하지_않았다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(false);
        SignUpUserRequest request = new SignUpUserRequest(user.getPhoneNumber(), user.getName(), "비밀번호");

        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(signUpVerification));

        // when and then
        assertThrows(VerifyingHasFailedException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, never()).existsByPhoneNumber(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void 이미_유저가_있다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        SignUpVerification signUpVerification = UserFixture.createSignUpVerification(true);
        SignUpUserRequest request = new SignUpUserRequest(user.getPhoneNumber(), user.getName(), "비밀번호");

        given(signUpVerificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(signUpVerification));
        given(userRepository.existsByPhoneNumber(request.getPhoneNumber())).willReturn(true);

        // when and then
        assertThrows(UserAlreadyExistsException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, times(1)).existsByPhoneNumber(request.getPhoneNumber());
        verify(userRepository, never()).save(any());
    }
}