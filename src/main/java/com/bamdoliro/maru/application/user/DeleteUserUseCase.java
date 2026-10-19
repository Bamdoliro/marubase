package com.bamdoliro.maru.application.user;

import com.bamdoliro.maru.application.auth.LogOutUseCase;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.service.UserFacade;
import com.bamdoliro.maru.domain.user.domain.value.Password;
import com.bamdoliro.maru.domain.user.exception.PasswordMismatchException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.user.dto.request.DeleteUserRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@UseCase
public class DeleteUserUseCase {

    private final FormRepository formRepository;
    private final LogOutUseCase logOutUseCase;
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    @Transactional
    public void execute(User user, DeleteUserRequest request) {
        user = userFacade.getUserByUuidForUpdate(user.getUuid());
        validatePassword(request.getPassword(), user.getPassword());
        Optional<Form> form = formRepository.findByUser(user);
        form.ifPresent(formRepository::delete);
        logOutUseCase.execute(user);
        userRepository.delete(user);
    }

    private void validatePassword(String actual, Password expected) {
        if (!expected.match(actual)) {
            throw new PasswordMismatchException();
        }
    }
}
