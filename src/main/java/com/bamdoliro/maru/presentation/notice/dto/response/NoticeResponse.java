package com.bamdoliro.maru.presentation.notice.dto.response;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponse {

    private final String title;
    private final String content;
    private final String fileUrl;
    private final LocalDateTime createdAt;
    
    public NoticeResponse(Notice notice) {
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.fileUrl = notice.getFileUrl();
        this.createdAt = notice.getCreatedAt();
    }
}
