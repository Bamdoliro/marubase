package com.bamdoliro.maru.presentation.auth;

import com.bamdoliro.maru.application.auth.LogInUseCase;
import com.bamdoliro.maru.application.auth.LogOutUseCase;
import com.bamdoliro.maru.application.auth.RefreshTokenUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.auth.dto.request.LogInRequest;
import com.bamdoliro.maru.presentation.auth.dto.response.TokenResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final LogInUseCase logInUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogOutUseCase logOutUseCase;

    @PostMapping
    public SingleCommonResponse<TokenResponse> logIn(@RequestBody @Valid LogInRequest request) {
        return CommonResponse.ok(logInUseCase.execute(request));
    }

    @PatchMapping
    public SingleCommonResponse<TokenResponse> refreshToken(@RequestHeader("Refresh-Token") @NotBlank String refreshToken) {
        return CommonResponse.ok(refreshTokenUseCase.execute(refreshToken));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void logOut(
            @AuthenticationPrincipal User user
    ) {
        logOutUseCase.execute(user);
    }
}
