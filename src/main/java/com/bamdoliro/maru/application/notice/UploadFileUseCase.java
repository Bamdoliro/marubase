package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.UploadFileResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@UseCase
public class UploadFileUseCase {

    private final FileService fileService;

    public UploadFileResponse execute() {
        String  uuid = UUID.randomUUID().toString();

        return new UploadFileResponse(fileService.getPresignedUrl(FolderConstant.NOTICE_FILE, uuid), uuid);
    }
}