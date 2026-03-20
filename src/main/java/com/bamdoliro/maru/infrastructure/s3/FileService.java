package com.bamdoliro.maru.infrastructure.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String getUploadPresignedUrl(String folder, String fileName, FileMetadata fileMetadata, FileValidator validator) {
        validator.validate(fileMetadata);
        String fullFileName = createFileName(folder, fileName);
        GeneratePresignedUrlRequest request = getGenerateUploadPresignedUrlRequest(bucket, fullFileName, fileMetadata);

        return amazonS3.generatePresignedUrl(request).toString();
    }

    public String getDownloadPresignedUrl(String folder, String fileName) {
        String fullFileName = createFileName(folder, fileName);
        GeneratePresignedUrlRequest request = getGenerateDownloadPresignedUrlRequest(bucket, fullFileName);

        return request != null ? amazonS3.generatePresignedUrl(request).toString() : null;
    }

    public UrlResponse getPresignedUrl(String folder, String fileName, FileMetadata metadata, FileValidator validator) {
        return new UrlResponse(
                getUploadPresignedUrl(folder, fileName, metadata, validator),
                getDownloadPresignedUrl(folder, fileName)
        );
    }

    private GeneratePresignedUrlRequest getGenerateUploadPresignedUrlRequest(String bucket, String fileName, FileMetadata fileMetadata) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration(3));

        request.putCustomRequestHeader(Headers.CONTENT_TYPE, fileMetadata.getMediaType());
        request.putCustomRequestHeader(Headers.CONTENT_LENGTH, fileMetadata.getFileSize().toString());

        return request;
    }

    private GeneratePresignedUrlRequest getGenerateDownloadPresignedUrlRequest(String bucket, String fileName) {
        try {
            amazonS3.getObjectMetadata(bucket, fileName);

            return new GeneratePresignedUrlRequest(bucket, fileName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(getPresignedUrlExpiration(60 * 10));
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return null;
            }
            throw e;
        }
    }

    private Date getPresignedUrlExpiration(int duration) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000L * 60 * duration;
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    private String createFileName(String folder, String fileName) {
        return folder + "/" + fileName;
    }
}