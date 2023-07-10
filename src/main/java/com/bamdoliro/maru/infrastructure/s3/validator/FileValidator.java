package com.bamdoliro.maru.infrastructure.s3.validator;

import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import com.bamdoliro.maru.infrastructure.s3.exception.TooLongNameException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@FunctionalInterface
public interface FileValidator {
    void customValidate(MultipartFile file);

    default void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        if (Objects.nonNull(file.getOriginalFilename()) && file.getOriginalFilename().length() > 20) {
            throw new TooLongNameException();
        }

        customValidate(file);
    }
}