package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.application.user.SendVerificationUseCase;
import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.application.user.UpdatePasswordUseCase;
import com.bamdoliro.maru.application.user.VerifyUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.user.dto.request.UpdatePasswordRequest;
import com.bamdoliro.maru.presentation.user.dto.request.SignUpUserRequest;
import com.bamdoliro.maru.presentation.user.dto.request.SendVerificationRequest;
import com.bamdoliro.maru.presentation.user.dto.request.VerifyRequest;
import com.bamdoliro.maru.presentation.user.dto.response.UserResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RequestMapping("/user")
@RestController
public class UserController {

    private final SignUpUserUseCase signUpUserUseCase;
    private final SendVerificationUseCase sendVerificationUseCase;
    private final VerifyUseCase verifyUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpUserRequest request) {
        signUpUserUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/verification")
    public void sendMessageVerification(
            @RequestBody @Valid SendVerificationRequest request
    ) {
        sendVerificationUseCase.execute(request);
    }

    @GetMapping
    public SingleCommonResponse<UserResponse> getUser(
            @AuthenticationPrincipal User user
    ) {
        return CommonResponse.ok(
                new UserResponse(user)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/verification")
    public void verify(
            @RequestBody @Valid VerifyRequest request
    ) {
        verifyUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/password-update")
    public void updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request
    ){
        updatePasswordUseCase.execute(request);
    }
}
