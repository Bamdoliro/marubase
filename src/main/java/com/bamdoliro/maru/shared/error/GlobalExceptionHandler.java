package com.bamdoliro.maru.shared.error;

import com.bamdoliro.maru.shared.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> bindException(BindException e) {
        Map<String, String> errorMap = new HashMap<>();

        for (FieldError error : e.getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(GlobalErrorProperty.BAD_REQUEST.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, errorMap));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> errorMap = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity
                .status(GlobalErrorProperty.BAD_REQUEST.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, errorMap));
    }

    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> missingRequestValueException(MissingRequestValueException e) {

        return ResponseEntity
                .status(GlobalErrorProperty.BAD_REQUEST.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> missingRequestValueException(MissingServletRequestPartException e) {

        return ResponseEntity
                .status(GlobalErrorProperty.BAD_REQUEST.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> enumMismatchException(MethodArgumentNotValidException e) {

        return ResponseEntity
                .status(GlobalErrorProperty.BAD_REQUEST.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, e.getBindingResult().getFieldErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<ErrorResponse> handleFileSizeLimitExceededException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<ErrorResponse> handleError(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResponse(GlobalErrorProperty.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MaruException.class)
    public ResponseEntity<ErrorResponse> handleMaruException(MaruException e) {
        return ResponseEntity
                .status(e.getErrorProperty().getStatus())
                .body(new ErrorResponse(e.getErrorProperty()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(GlobalErrorProperty.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ErrorResponse(GlobalErrorProperty.INTERNAL_SERVER_ERROR));
    }
}