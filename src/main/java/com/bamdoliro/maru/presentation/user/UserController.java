package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.application.user.SendEmailVerificationUseCase;
import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

    private final SignUpUserUseCase signUpUserUseCase;
    private final SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpUserRequest request) {
        signUpUserUseCase.execute(request);
    }

    @PostMapping("/verification")
    public void sendEmailVerification(@RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        sendEmailVerificationUseCase.execute(email);
    }
}
