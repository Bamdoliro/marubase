package com.bamdoliro.maru.infrastructure.s3.validator;

import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import com.bamdoliro.maru.infrastructure.s3.exception.InvalidFileNameException;
import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface FileValidator {
    void customValidate(MultipartFile file);

    default void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        if (file.getOriginalFilename() == null ||
                file.getOriginalFilename().isBlank() ||
                file.getOriginalFilename().length() > 20
        ) {
            throw new InvalidFileNameException();
        }

        customValidate(file);
    }
}