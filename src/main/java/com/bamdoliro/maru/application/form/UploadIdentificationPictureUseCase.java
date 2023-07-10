package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.exception.ImageSizeMismatchException;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RequiredArgsConstructor
@UseCase
public class UploadIdentificationPictureUseCase {

    private final UploadFileService uploadFileService;

    public UploadResponse execute(MultipartFile image) {
        return uploadFileService.execute(image, FolderConstant.IDENTIFICATION_PICTURE, file -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                if (!(bufferedImage.getWidth() == 117 && bufferedImage.getHeight() == 156)) {
                    throw new ImageSizeMismatchException();
                }
            } catch (IOException e) {
                throw new FailedToSaveException();
            }
        });
    }
}
