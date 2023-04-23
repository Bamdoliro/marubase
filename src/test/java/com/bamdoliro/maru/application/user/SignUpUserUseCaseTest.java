package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserAlreadyExistsException;
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
    private UserRepository userRepository;

    @Test
    void 유저를_생성한다() {
        // given
        User user = UserFixture.createUser();
        SignUpUserRequest request = new SignUpUserRequest(user.getEmail(), "비밀번호");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        given(userRepository.save(any(User.class))).willReturn(user);
        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);

        // when
        signUpUserUseCase.execute(request);

        // then
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void 이미_유저가_있다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        SignUpUserRequest request = new SignUpUserRequest(user.getEmail(), "비밀번호");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when and then
        assertThrows(UserAlreadyExistsException.class,
                () -> signUpUserUseCase.execute(request));

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any());
    }
}