package com.bamdoliro.maru.presentation.notice.dto.response;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeSimpleResponse {

    private final String title;
    private final LocalDateTime createdAt;

    public NoticeSimpleResponse(Notice notice) {
        this.title = notice.getTitle();
        this.createdAt = notice.getCreatedAt();
    }
}
