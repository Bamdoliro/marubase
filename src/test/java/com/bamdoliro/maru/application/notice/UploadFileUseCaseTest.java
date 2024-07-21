package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UploadFileUseCaseTest {

    @InjectMocks
    private UploadFileUseCase uploadFileUseCase;

    @Mock
    private UploadFileService uploadFileService;

    @Test
    void 공지사항_파일을_업로드한다() {
        // given
        User user = UserFixture.createAdminUser();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "<<file>>".getBytes(StandardCharsets.UTF_8)
        );
        given(uploadFileService.execute(any(MultipartFile.class), any(String.class), any(String.class), any(FileValidator.class))).willReturn(new UploadResponse("https://host.com/notice.pdf"));

        // when
        uploadFileUseCase.execute(user, file);

        // then
        verify(uploadFileService, times(1)).execute(any(MultipartFile.class), any(String.class), any(String.class), any(FileValidator.class));
    }
}
