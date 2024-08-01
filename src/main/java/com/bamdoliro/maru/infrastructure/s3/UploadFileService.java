package com.bamdoliro.maru.infrastructure.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.shared.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private final S3Properties s3Properties;
    private final AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public UploadResponse execute(MultipartFile file, String folder, String fileName, FileValidator validator) {
        validator.validate(file);
        String fullFileName = createFileName(folder, fileName);

        try {
            PutObjectRequest request = new PutObjectRequest(
                    s3Properties.getBucket(),
                    fullFileName,
                    file.getInputStream(),
                    getObjectMetadata(file)
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3Client.putObject(request);
        } catch (SdkClientException | IOException e) {
            throw new FailedToSaveException();
        }

        return new UploadResponse(
                amazonS3Client.getUrl(s3Properties.getBucket(), fullFileName).toString()
        );
    }

    public UploadResponse getUploadPresignedUrl(String folder, String fileName) {
        String fullFileName = createFileName(folder, fileName);
        GeneratePresignedUrlRequest request = getGenerateUploadPresignedUrlRequest(bucket, fullFileName);

        return new UploadResponse(
                amazonS3Client.generatePresignedUrl(request).toString()
        );
    }

    public UploadResponse getDownloadPresignedUrl(String folder, String fileName) {
        String fullFileName = createFileName(folder, fileName);
        GeneratePresignedUrlRequest request = getGenerateDownloadPresignedUrlRequest(bucket, fullFileName);

        return new UploadResponse(
                request != null ? amazonS3Client.generatePresignedUrl(request).toString() : null
        );
    }

    private GeneratePresignedUrlRequest getGenerateUploadPresignedUrlRequest(String bucket, String fileName) {
        return new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration(1));
    }

    private GeneratePresignedUrlRequest getGenerateDownloadPresignedUrlRequest(String bucket, String fileName) {
        try {
            amazonS3Client.getObjectMetadata(bucket, fileName);

            return new GeneratePresignedUrlRequest(bucket, fileName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(getPresignedUrlExpiration(60 * 10));
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
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

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }
}