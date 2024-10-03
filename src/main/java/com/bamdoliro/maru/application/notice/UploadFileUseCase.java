package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.presentation.notice.dto.request.UploadFileRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.UploadFileResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
@UseCase
public class UploadFileUseCase {

    private final FileService fileService;

    public List<UploadFileResponse> execute(UploadFileRequest request) {
        List<String> fileNameList = request.getFileNameList().stream()
                .map(fileName -> UUID.randomUUID() + "_" + fileName)
                .toList();

        return fileNameList.stream()
                .map(fileName -> new UploadFileResponse(
                        fileService.getPresignedUrl(FolderConstant.NOTICE_FILE, fileName),
                        fileName
                ))
                .toList();
    }
}