package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class DeleteNoticeUseCase {

    private final NoticeRepository noticeRepository;

    public void execute(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
}
