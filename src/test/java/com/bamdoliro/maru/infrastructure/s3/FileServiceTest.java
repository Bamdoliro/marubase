package com.bamdoliro.maru.infrastructure.s3;

import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FileStorageAccessFailedException;
import com.bamdoliro.maru.infrastructure.s3.exception.error.S3ErrorProperty;
import com.bamdoliro.maru.infrastructure.s3.validator.DefaultFileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.util.Set;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Test
    void Presigned_URL을_생성한다() throws Exception {
        // given
        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/random-uuid-image.png";
        FileMetadata fileMetadata = new FileMetadata(
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                MB
        );

        PresignedPutObjectRequest putPresignedRequest = mock(PresignedPutObjectRequest.class);
        given(putPresignedRequest.url()).willReturn(new URL(url));

        PresignedGetObjectRequest getPresignedRequest = mock(PresignedGetObjectRequest.class);
        given(getPresignedRequest.url()).willReturn(new URL(url));

        given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(HeadObjectResponse.builder().build());
        given(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(putPresignedRequest);
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(getPresignedRequest);

        // when
        UrlResponse response = fileService.getPresignedUrl("folder", "uuid", fileMetadata, metadata ->
                DefaultFileValidator.validate(metadata, Set.of(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG), 2)
        );

        // then
        verify(s3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void 파일을_다운로드한다() {
        // given
        byte[] file = "pdf".getBytes();
        given(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).willReturn(
                ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), file)
        );

        // when
        byte[] result = fileService.download("form", "uuid");

        // then
        assertArrayEquals(file, result);
        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void 파일이_존재하면_다운로드_Presigned_URL을_반환한다() throws Exception {
        // given
        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/form/uuid";
        PresignedGetObjectRequest getPresignedRequest = mock(PresignedGetObjectRequest.class);
        given(getPresignedRequest.url()).willReturn(new URL(url));
        given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(HeadObjectResponse.builder().build());
        given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(getPresignedRequest);

        // when
        String result = fileService.getDownloadPresignedUrl("form", "uuid");

        // then
        assertEquals(url, result);
    }

    @Test
    void 파일이_없으면_다운로드_Presigned_URL로_null을_반환한다() {
        // given
        given(s3Client.headObject(any(HeadObjectRequest.class))).willThrow(
                NoSuchKeyException.builder().statusCode(404).build()
        );

        // when
        String result = fileService.getDownloadPresignedUrl("form", "uuid");

        // then
        assertNull(result);
        verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void 상태_코드가_404인_S3_예외이면_다운로드_Presigned_URL로_null을_반환한다() {
        // given
        given(s3Client.headObject(any(HeadObjectRequest.class))).willThrow(
                S3Exception.builder().statusCode(404).build()
        );

        // when
        String result = fileService.getDownloadPresignedUrl("form", "uuid");

        // then
        assertNull(result);
        verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void 파일_존재_확인이_403으로_실패하면_파일_저장소_에러가_발생한다(CapturedOutput output) {
        // given
        ReflectionTestUtils.setField(fileService, "bucket", "maru-bucket");
        given(s3Client.headObject(any(HeadObjectRequest.class))).willThrow(
                S3Exception.builder()
                        .statusCode(403)
                        .requestId("request-id")
                        .awsErrorDetails(AwsErrorDetails.builder().errorCode("AccessDenied").build())
                        .build()
        );

        // when
        FileStorageAccessFailedException exception = assertThrows(
                FileStorageAccessFailedException.class,
                () -> fileService.getDownloadPresignedUrl("form", "uuid")
        );

        // then
        assertEquals(S3ErrorProperty.FILE_STORAGE_ACCESS_FAILED, exception.getErrorProperty());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getErrorProperty().getStatus());
        assertEquals("파일 저장소 요청에 실패했습니다.", exception.getMessage());
        verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));

        assertTrue(output.getOut().contains(
                "bucket: maru-bucket, key: form/uuid, status: 403, errorCode: AccessDenied, requestId: request-id"
        ));
        assertFalse(output.getOut().contains("X-Amz-"));
    }
}
