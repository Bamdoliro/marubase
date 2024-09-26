package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UpdateNoticeUseCase {

    private final NoticeFacade noticeFacade;

    @Transactional
    public void execute(Long id, NoticeRequest request) {
        Notice notice = noticeFacade.getNotice(id);
        notice.update(request.getTitle(), request.getContent(), request.getFileNameList());
    }
}
