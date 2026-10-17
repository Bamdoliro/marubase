package com.bamdoliro.maru.shared.ratelimit;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitPolicy {

    LOGIN_ACCOUNT("login-account", 10, Duration.ofMinutes(10)),
    VERIFICATION_SEND_PHONE("verification-send-phone", 3, Duration.ofMinutes(10)),
    VERIFICATION_CHECK_PHONE("verification-check-phone", 30, Duration.ofMinutes(10)),
    VERIFICATION_FAILURE("verification-failure", 5, Duration.ofMinutes(5)),
    PASSWORD_UPDATE_PHONE("password-update-phone", 5, Duration.ofMinutes(10)),
    FAIR_APPLICATION_PHONE("fair-application-phone", 5, Duration.ofMinutes(10));

    private final String keyPrefix;
    private final long maxRequests;
    private final Duration window;
}
