package com.bamdoliro.maru.presentation.notice.dto.response;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeSimpleResponse {

    private final Long id;
    private final String title;
    private final LocalDateTime updatedAt;

    public NoticeSimpleResponse(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.updatedAt = notice.getUpdatedAt();
    }
}
