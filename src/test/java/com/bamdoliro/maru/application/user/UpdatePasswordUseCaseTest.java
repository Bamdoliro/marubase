package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.infrastructure.persistence.user.UpdatePasswordVerificationRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdatePasswordUseCaseTest {

    @InjectMocks
    private UpdatePasswordUseCase updatePasswordUseCase;

    @Mock
    private UpdatePasswordVerificationRepository verificationRepository;

    @Mock
    private UserFacade userFacade;

    @Mock
    private AuthSessionService authSessionService;

    @Test
    void 비밀번호를_변경한다() {
        //given
        User user = UserFixture.createUser();
        UpdatePasswordVerification verification = UserFixture.createUpdatePasswordVerification(true);
        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPhoneNumber(), "비밀번호");

        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(verification));
        given(userFacade.getUserForUpdate(request.getPhoneNumber())).willReturn(user);

        //when
        updatePasswordUseCase.execute(request);

        //then
        verify(verificationRepository, times(1)).findById(request.getPhoneNumber());

        verify(authSessionService).revoke(user.getUuid());
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

        verify(userFacade, never()).getUserForUpdate(any());
    }

    @Test
    void 세션_폐기에_실패하면_비밀번호를_바꾸지_않는다() {
        User user = UserFixture.createUser();
        String previousPassword = user.getPassword().getValue();
        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPhoneNumber(), "새비밀번호");
        given(verificationRepository.findById(request.getPhoneNumber()))
                .willReturn(Optional.of(UserFixture.createUpdatePasswordVerification(true)));
        given(userFacade.getUserForUpdate(request.getPhoneNumber())).willReturn(user);
        doThrow(new IllegalStateException("Redis unavailable")).when(authSessionService).revoke(user.getUuid());
        assertThrows(IllegalStateException.class, () -> updatePasswordUseCase.execute(request));
        org.junit.jupiter.api.Assertions.assertEquals(previousPassword, user.getPassword().getValue());
    }

    @Test
    void 전화번호_인증을_하지_않았다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        UpdatePasswordVerification verification = UserFixture.createUpdatePasswordVerification(false);
        UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPhoneNumber(), "비밀번호");

        given(verificationRepository.findById(request.getPhoneNumber())).willReturn(Optional.of(verification));

        // when and then
        assertThrows(VerifyingHasFailedException.class,
                () -> updatePasswordUseCase.execute(request));

        verify(userFacade, never()).getUserForUpdate(any());
    }
}
