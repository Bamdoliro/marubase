package com.bamdoliro.maru.shared.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private String code;

    public ErrorResponse(ErrorProperty errorProperty) {
        this.message = errorProperty.getMessage();
        this.code = errorProperty.name();
    }
}