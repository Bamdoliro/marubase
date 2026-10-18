package com.bamdoliro.maru.presentation.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerificationResponse {

    private final String verificationToken;
    private final long expiresIn;
}
