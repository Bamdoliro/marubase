package com.bamdoliro.maru.shared.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaruException.class)
    public ResponseEntity<ErrorResponse> handleMaruException(MaruException e) {
        return ResponseEntity
                .status(e.getErrorProperty().getStatus())
                .body(new ErrorResponse(e.getErrorProperty()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity
                .status(GlobalErrorProperty.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.INTERNAL_SERVER_ERROR));
    }
}