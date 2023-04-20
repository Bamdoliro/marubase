package com.bamdoliro.maru.shared.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class MaruException extends RuntimeException {

    private final ErrorProperty errorProperty;
}
