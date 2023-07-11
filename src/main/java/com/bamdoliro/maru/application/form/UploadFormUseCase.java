package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.MediaTypeMismatchException;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import static com.bamdoliro.maru.shared.constants.FileConstants.MB;

@RequiredArgsConstructor
@UseCase
public class UploadFormUseCase {

    private final UploadFileService uploadFileService;

    public UploadResponse execute(MultipartFile form) {
        return uploadFileService.execute(form, FolderConstant.FORM, file -> {
            if (file.getContentType() != null && !file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
                throw new MediaTypeMismatchException();
            }

            if (file.getSize() > 10 * MB) {
                throw new FileSizeLimitExceededException();
            }
        });
    }
}
