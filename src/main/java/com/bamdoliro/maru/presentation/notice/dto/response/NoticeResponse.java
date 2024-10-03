package com.bamdoliro.maru.presentation.notice.dto.response;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class NoticeResponse {

    private final String title;
    private final String content;
    private final List<DownloadFileResponse> fileList;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public NoticeResponse(Notice notice, List<DownloadFileResponse> fileList) {
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.fileList = fileList;
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }
}
