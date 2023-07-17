package com.bamdoliro.maru.infrastructure.s3.validator;

import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface FileValidator {
    void customValidate(MultipartFile file);

    default void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        customValidate(file);
    }
}