package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.ImageSizeMismatchException;
import com.bamdoliro.maru.infrastructure.s3.exception.MediaTypeMismatchException;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;

@RequiredArgsConstructor
@UseCase
public class UploadIdentificationPictureUseCase {

    private final UploadFileService uploadFileService;

    public UploadResponse execute(User user, MultipartFile image) {
        return uploadFileService.execute(image, FolderConstant.IDENTIFICATION_PICTURE, user.getUuid().toString(), file -> {
            if (file.getContentType() != null && !(
                    file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) ||
                            file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)
            )) {
                throw new MediaTypeMismatchException();
            }

            try {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                if (!(bufferedImage.getWidth() == 117 && bufferedImage.getHeight() == 156)) {
                    throw new ImageSizeMismatchException();
                }
            } catch (IOException e) {
                throw new FailedToSaveException();
            }

            if (file.getSize() > 2 * MB) {
                throw new FileSizeLimitExceededException();
            }
        });
    }
}
