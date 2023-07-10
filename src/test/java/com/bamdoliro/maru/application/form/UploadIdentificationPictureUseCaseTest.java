package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.ImageSizeMismatchException;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadIdentificationPictureUseCaseTest {

    @InjectMocks
    private UploadIdentificationPictureUseCase uploadIdentificationPictureUseCase;

    @Mock
    private UploadFileService uploadFileService;

    @Test
    void 증명_사진을_업로드한다() {
        // given
        String fileName = "identification-picture.png";
        MockMultipartFile image = new MockMultipartFile(
                "image",
                fileName,
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes(StandardCharsets.UTF_8)
        );
        given(uploadFileService.execute(any(MultipartFile.class), any(String.class), any(FileValidator.class))).willReturn(new UploadResponse("https://host.com/image.png"));

        // when
        uploadIdentificationPictureUseCase.execute(image);

        // then
        verify(uploadFileService, times(1)).execute(any(MultipartFile.class), any(String.class), any(FileValidator.class));
    }

    @Test
    void 증명_사진의_크기가_크면_에러가_발생한다() {
        // given
        String fileName = "identification-picture-big.png";
        MockMultipartFile image = new MockMultipartFile(
                "image",
                fileName,
                MediaType.IMAGE_PNG_VALUE,
                "<<image>>".getBytes(StandardCharsets.UTF_8)
        );
        willThrow(ImageSizeMismatchException.class).given(uploadFileService).execute(any(MultipartFile.class), any(String.class), any(FileValidator.class));

        // when and then
        assertThrows(ImageSizeMismatchException.class,
                () -> uploadIdentificationPictureUseCase.execute(image));

        verify(uploadFileService, times(1)).execute(any(MultipartFile.class), any(String.class), any(FileValidator.class));
    }
}