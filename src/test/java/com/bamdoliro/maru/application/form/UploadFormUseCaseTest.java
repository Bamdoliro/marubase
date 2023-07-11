package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadFormUseCaseTest {

    @InjectMocks
    private UploadFormUseCase uploadFormUseCase;

    @Mock
    private UploadFileService uploadFileService;

    @Test
    void 원서_서류를_업로드한다() {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes(StandardCharsets.UTF_8)
        );
        given(uploadFileService.execute(any(MultipartFile.class), any(String.class), any(FileValidator.class))).willReturn(new UploadResponse("https://host.com/image.pdf"));

        // when
        uploadFormUseCase.execute(image);

        // then
        verify(uploadFileService, times(1)).execute(any(MultipartFile.class), any(String.class), any(FileValidator.class));
    }
}