package com.bamdoliro.maru.infrastructure.s3;

import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

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
        } catch (NoSuchKeyException e) {
            return null;
        }

        return GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60L * 10))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build())
                .build();
    }

    private String createFileName(String folder, String fileName) {
        return folder + "/" + fileName;
    }
}