package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.presentation.notice.dto.request.UploadFileRequest;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UploadFileUseCaseTest {

    @InjectMocks
    private UploadFileUseCase uploadFileUseCase;

    @Mock
    private FileService fileService;

    @Test
    void 공지사항_파일을_업로드한다() {
        // given
        given(fileService.getPresignedUrl(any(String.class), any(String.class))).willReturn(SharedFixture.createNoticeFileUrlResponse());
        UploadFileRequest request = new UploadFileRequest(List.of("notice-file.pdf"));

        // when
        uploadFileUseCase.execute(request);

        // then
        verify(fileService, times(1)).getPresignedUrl(any(String.class), any(String.class));
    }
}
