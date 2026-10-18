package com.bamdoliro.maru.application.auth;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.auth.service.AuthSessionService;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class LogOutUseCase {

    private final AuthSessionService authSessionService;
    private final UserFacade userFacade;

    @Transactional
    public void execute(User user) {
        userFacade.getUserByUuidForUpdate(user.getUuid());
        authSessionService.revoke(user.getUuid());
    }
}
