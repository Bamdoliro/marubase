package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class QueryNoticeUseCase {

    private final NoticeFacade noticeFacade;

    public NoticeResponse execute(Long id) {
        return new NoticeResponse(
                noticeFacade.getNotice(id)
        );
    }
}
