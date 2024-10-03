package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.response.IdResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class CreateNoticeUseCase {

    private final NoticeRepository noticeRepository;

    public IdResponse execute(NoticeRequest request) {
        List<String> fileNameList = request.getFileNameList();
        Notice notice = noticeRepository.save(
                new Notice(request.getTitle(), request.getContent(), fileNameList)
        );

        return new IdResponse(notice);
    }
}
