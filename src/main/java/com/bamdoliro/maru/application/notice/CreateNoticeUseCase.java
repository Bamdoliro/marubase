package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CreateNoticeUseCase {

    private final NoticeRepository noticeRepository;

    public void execute(@Valid CreateQuestionRequest request) {
        noticeRepository.save(
                new Notice(request.getTitle(), request.getContent(), request.getCategory()));
    }

}
