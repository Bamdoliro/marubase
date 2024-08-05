package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryNoticeUseCase {

    private final NoticeFacade noticeFacade;
    private final FileService fileService;

    public NoticeResponse execute(Long id) {
        Notice notice = noticeFacade.getNotice(id);
        String fileUrl = fileService.getPresignedUrl(FolderConstant.NOTICE_FILE, notice.getFileUuid().toString()).getDownloadUrl();

        return new NoticeResponse(notice, fileUrl);
    }
}
