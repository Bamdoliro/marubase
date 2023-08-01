package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.domain.notice.exception.NoticeNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NoticeFacade {

    private final NoticeRepository noticeRepository;

    public Notice getNotice(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(NoticeNotFoundException::new);
    }
}
