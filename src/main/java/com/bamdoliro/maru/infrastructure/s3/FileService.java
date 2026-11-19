package com.bamdoliro.maru.infrastructure.s3;

import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FileStorageAccessFailedException;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.prefix:}")
    private String prefix;

    public String getUploadPresignedUrl(String folder, String fileName, FileMetadata fileMetadata, FileValidator validator) {
        validator.validate(fileMetadata);
        String fullFileName = createFileName(folder, fileName);
        PutObjectPresignRequest request = getGenerateUploadPresignedUrlRequest(bucket, fullFileName, fileMetadata);

        return s3Presigner.presignPutObject(request).url().toString();
    }

    public String getDownloadPresignedUrl(String folder, String fileName) {
        String fullFileName = createFileName(folder, fileName);
        GetObjectPresignRequest request = getGenerateDownloadPresignedUrlRequest(bucket, fullFileName);

        return request != null ? s3Presigner.presignGetObject(request).url().toString() : null;
    }

    public byte[] download(String folder, String fileName) {
        String fullFileName = createFileName(folder, fileName);
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(fullFileName)
                        .build()
        );

        return response.asByteArray();
    }

    public UrlResponse getPresignedUrl(String folder, String fileName, FileMetadata metadata, FileValidator validator) {
        return new UrlResponse(
                getUploadPresignedUrl(folder, fileName, metadata, validator),
                getDownloadPresignedUrl(folder, fileName)
        );
    }

    private PutObjectPresignRequest getGenerateUploadPresignedUrlRequest(String bucket, String fileName, FileMetadata fileMetadata) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(fileMetadata.getMediaType())
                .contentLength(fileMetadata.getFileSize())
                .build();

        return PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(objectRequest)
                .build();
    }

    private GetObjectPresignRequest getGenerateDownloadPresignedUrlRequest(String bucket, String fileName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build());
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return null;
            }

            log.error("S3 파일 존재 확인에 실패했습니다. bucket: {}, key: {}, status: {}, errorCode: {}, requestId: {}",
                    bucket, fileName, e.statusCode(), getErrorCode(e), e.requestId());
            throw new FileStorageAccessFailedException();
        }

        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60L * 10))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build())
                .build();
    }

    private String getErrorCode(S3Exception e) {
        return e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null;
    }

    private String createFileName(String folder, String fileName) {
        String base = folder + "/" + fileName;
        return (prefix == null || prefix.isBlank()) ? base : prefix + "/" + base;
    }
}
