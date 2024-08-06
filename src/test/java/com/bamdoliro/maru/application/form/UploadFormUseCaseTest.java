package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadFormUseCaseTest {

    @InjectMocks
    private UploadFormUseCase uploadFormUseCase;

    @Mock
    private FileService fileService;

    @Test
    void 원서_서류를_업로드한다() {
        // given
        User user = UserFixture.createUser();
        given(fileService.getPresignedUrl(any(String.class), any(String.class))).willReturn(SharedFixture.createFormUrlResponse());

        // when
        uploadFormUseCase.execute(user);

        // then
        verify(fileService, times(1)).getPresignedUrl(any(String.class), any(String.class));
    }
}