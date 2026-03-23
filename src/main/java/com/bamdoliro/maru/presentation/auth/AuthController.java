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
import com.bamdoliro.maru.shared.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final LogInUseCase logInUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogOutUseCase logOutUseCase;
    private final CookieUtil cookieUtil;

    @PostMapping
    public SingleCommonResponse<TokenResponse> logIn(
            @RequestBody @Valid LogInRequest request,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = logInUseCase.execute(request);

        cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());
        cookieUtil.addRefreshTokenCookie(response, tokenResponse.getRefreshToken());

        return CommonResponse.ok(tokenResponse);
    }

    @PatchMapping
    public SingleCommonResponse<TokenResponse> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshTokenFromHeader,
            HttpServletResponse response
    ) {
        String refreshToken = refreshTokenFromCookie != null
                ? refreshTokenFromCookie
                : refreshTokenFromHeader;

        TokenResponse tokenResponse = refreshTokenUseCase.execute(refreshToken);

        cookieUtil.addAccessTokenCookie(response, tokenResponse.getAccessToken());

        return CommonResponse.ok(tokenResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void logOut(
            @AuthenticationPrincipal User user,
            HttpServletResponse response
    ) {
        logOutUseCase.execute(user);

        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
    }
}
