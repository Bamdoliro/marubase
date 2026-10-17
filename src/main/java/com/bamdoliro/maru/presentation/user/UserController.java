package com.bamdoliro.maru.presentation.user;

import com.bamdoliro.maru.application.user.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.user.dto.request.*;
import com.bamdoliro.maru.presentation.user.dto.response.UserResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.ratelimit.RateLimitPolicy;
import com.bamdoliro.maru.shared.ratelimit.RateLimited;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
@RestController
public class UserController {

    private final SignUpUserUseCase signUpUserUseCase;
    private final SendVerificationUseCase sendVerificationUseCase;
    private final VerifyUseCase verifyUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpUserRequest request) {
        signUpUserUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/verification")
    @RateLimited(policy = RateLimitPolicy.VERIFICATION_SEND_PHONE, key = "#p0.phoneNumber")
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
    @RateLimited(policy = RateLimitPolicy.VERIFICATION_CHECK_PHONE, key = "#p0.phoneNumber")
    public void verify(
            @RequestBody @Valid VerifyRequest request
    ) {
        verifyUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/password")
    @RateLimited(policy = RateLimitPolicy.PASSWORD_UPDATE_PHONE, key = "#p0.phoneNumber")
    public void updatePassword(
            @RequestBody @Valid UpdatePasswordRequest request
    ){
        updatePasswordUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteUser(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid DeleteUserRequest request
    ) {
        deleteUserUseCase.execute(user, request);
    }
}
