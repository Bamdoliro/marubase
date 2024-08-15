package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.presentation.notice.dto.request.UploadFileRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.UploadFileResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@UseCase
public class UploadFileUseCase {

    private final FileService fileService;

    public UploadFileResponse execute(UploadFileRequest request) {
        String  fileName = UUID.randomUUID() + "_" + request.getFileName();

        return new UploadFileResponse(fileService.getPresignedUrl(FolderConstant.NOTICE_FILE, fileName), fileName);
    }
}