package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QueryNoticeListUseCase {

    private final NoticeRepository noticeRepository;

    public List<NoticeSimpleResponse> execute() {
        return noticeRepository.findAll()
                .stream()
                .map(NoticeSimpleResponse::new)
                .toList();
    }
}
