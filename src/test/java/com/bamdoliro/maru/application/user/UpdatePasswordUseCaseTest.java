package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.VerificationType;
import com.bamdoliro.maru.domain.user.exception.VerifyingHasFailedException;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.domain.user.service.VerificationProofService;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdatePasswordUseCaseTest {

    private final VerificationProofService proofs = mock(VerificationProofService.class);
    private final UserFacade users = mock(UserFacade.class);
    private final AuthSessionService sessions = mock(AuthSessionService.class);
    private final UpdatePasswordUseCase useCase = new UpdatePasswordUseCase(proofs, users, sessions);
    private final User user = UserFixture.createUser();
    private final UpdatePasswordRequest request = new UpdatePasswordRequest(user.getPhoneNumber(), "changed", "proof");

    @Test
    void 증명_소비와_잠금_세션_폐기_후_비밀번호를_변경한다() {
        when(users.getUserForUpdate(user.getPhoneNumber())).thenReturn(user);
        useCase.execute(request);
        var order = inOrder(proofs, users, sessions);
        order.verify(proofs).consume(user.getPhoneNumber(), VerificationType.UPDATE_PASSWORD, "proof");
        order.verify(users).getUserForUpdate(user.getPhoneNumber());
        order.verify(sessions).revoke(user.getUuid());
        assertTrue(user.getPassword().match("changed"));
    }

    @Test
    void 인증_증명이_없으면_변경하지_않는다() {
        doThrow(new VerifyingHasFailedException()).when(proofs)
                .consume(user.getPhoneNumber(), VerificationType.UPDATE_PASSWORD, "proof");
        assertThrows(VerifyingHasFailedException.class, () -> useCase.execute(request));
        verifyNoInteractions(users, sessions);
    }

    @Test
    void 세션_폐기에_실패하면_비밀번호를_바꾸지_않는다() {
        String previous = user.getPassword().getValue();
        when(users.getUserForUpdate(user.getPhoneNumber())).thenReturn(user);
        doThrow(new IllegalStateException("Redis unavailable")).when(sessions).revoke(user.getUuid());
        assertThrows(IllegalStateException.class, () -> useCase.execute(request));
        assertEquals(previous, user.getPassword().getValue());
    }
}
