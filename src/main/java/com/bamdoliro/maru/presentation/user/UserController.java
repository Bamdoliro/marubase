package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final SignUpUserUseCase signUpUserUseCase;

    @PostMapping
    public void signUp(@RequestBody @Valid SignUpUserRequest request) {
        signUpUserUseCase.execute(request);
    }
}
