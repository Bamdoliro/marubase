package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.VerificationRepository;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdatePasswordUseCaseTest {

    @InjectMocks
    private UpdatePasswordUseCase updatePasswordUseCase;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFacade userFacade;

    @Test
    void 비밀번호를_변경한다() {
        //given
        User user = UserFixture.createUser();
        System.out.println(user.getPassword());
        Verification verification = UserFixture.createVerification(true);
        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPhoneNumber(), "비밀번호");

        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(verification));
        given(userRepository.existsByPhoneNumber(user.getPhoneNumber())).willReturn(true);
        given(userFacade.getUser(request.getPhoneNumber())).willReturn(user);

        //when
        updatePasswordUseCase.execute(request);

        //then
        verify(verificationRepository, times(1)).findById(request.getPhoneNumber());
        verify(userRepository, times(1)).existsByPhoneNumber(request.getPhoneNumber());
        verify(userFacade, times(1)).getUser(request.getPhoneNumber());
        verify(verificationRepository, times(1)).updateVerification(request.getPhoneNumber(), false);

        assertTrue(user.getPassword().match(request.getPassword()));
    }

    @Test
    void 전화번호_인증을_요청하지_않았거나_만료되었다면_에러가_발생한다() {
        // given
        UpdatePasswordRequest request = new UpdatePasswordRequest("01011111111", "비밀번호");

        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.empty());

        // when and then
        assertThrows(VerifyingHasFailedException.class,
                () -> updatePasswordUseCase.execute(request));

        verify(userRepository, never()).existsByPhoneNumber(any());
        verify(userFacade, never()).getUser(any());
        verify(verificationRepository, never()).updateVerification(any(), anyBoolean());
    }

    @Test
    void 전화번호_인증을_하지_않았다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        Verification verification = UserFixture.createVerification(false);
        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPhoneNumber(), "비밀번호");

        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(verification));

        // when and then
        assertThrows(VerifyingHasFailedException.class,
                () -> updatePasswordUseCase.execute(request));

        verify(userRepository, never()).existsByPhoneNumber(any());
        verify(userFacade, never()).getUser(any());
        verify(verificationRepository, never()).updateVerification(any(), anyBoolean());
    }
}
