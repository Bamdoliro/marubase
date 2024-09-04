package com.bamdoliro.maru.presentation.notice.dto.response;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponse {

    private final String title;
    private final String content;
    private final String fileName;
    private final String fileUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public NoticeResponse(Notice notice, String fileUrl) {
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.fileName = notice.getFileName();
        this.fileUrl = fileUrl;
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }
}
