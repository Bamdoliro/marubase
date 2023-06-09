package com.bamdoliro.maru.infrastructure.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.validator.FileValidator;
import com.bamdoliro.maru.shared.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private final S3Properties s3Properties;
    private final AmazonS3Client amazonS3Client;

    public UploadResponse execute(MultipartFile file, String folder, FileValidator validator) {
        validator.validate(file);
        String fileName = createFileName(folder, file.getOriginalFilename());

        try {
            PutObjectRequest request = new PutObjectRequest(
                    s3Properties.getBucket(),
                    fileName,
                    file.getInputStream(),
                    getObjectMetadata(file)
            );
            amazonS3Client.putObject(request.withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (SdkClientException | IOException e) {
            throw new FailedToSaveException();
        }

        return new UploadResponse(
                amazonS3Client.getUrl(s3Properties.getBucket(), fileName).toString()
        );
    }

    private String createFileName(String folder, String originalName) {
        return folder + "/" + UUID.randomUUID() + "-" + originalName;
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }
}