package com.bamdoliro.maru.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.validator.DefaultFileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.net.URL;
import java.util.Set;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private AmazonS3 amazonS3;

    @Test
    void Presigned_URL을_생성한다() throws Exception {
        // given
        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/random-uuid-image.png";
        FileMetadata fileMetadata = new FileMetadata(
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                MB
        );
        given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).willReturn(new URL(url));

        // when
        UrlResponse response = fileService.getPresignedUrl("folder", "uuid", fileMetadata, metadata ->
                DefaultFileValidator.validate(metadata, Set.of(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG), 2)
        );

        // then
        verify(amazonS3, times(2)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }
}