package com.bamdoliro.maru.domain.user.service;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserRepository userRepository;

    @Test
    void 유저를_가져온다() {
        // given
        User user = UserFixture.createUser();
        given(userRepository.findByPhoneNumber(user.getPhoneNumber())).willReturn(Optional.of(user));

        // when
        User foundUser = userFacade.getUser(user.getPhoneNumber());

        // then
        assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber());
    }

    @Test
    void 존재하지_않는_유저일_때_에러가_발생한다() {
        // given
        given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());

        // when and then
        assertThrows(UserNotFoundException.class, () -> userFacade.getUser("저는없는데요"));
    }
}