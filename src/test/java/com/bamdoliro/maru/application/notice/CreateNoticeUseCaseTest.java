package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import com.bamdoliro.maru.shared.response.IdResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateNoticeUseCaseTest {

    @InjectMocks
    private CreateNoticeUseCase createNoticeUseCase;

    @Mock
    private NoticeRepository noticeRepository;

    @Test
    void 유저가_공지사항을_생성한다() {
        // given
        Notice notice = NoticeFixture.createNotice();
        NoticeRequest request = new NoticeRequest(notice.getTitle(), notice.getContent(), List.of(UUID.randomUUID().toString()));
        ArgumentCaptor<Notice> captor = ArgumentCaptor.forClass(Notice.class);

        given(noticeRepository.save(any(Notice.class))).willReturn(notice);

        // when
        IdResponse response = createNoticeUseCase.execute(request);

        // then
        verify(noticeRepository, times(1)).save(captor.capture());
        Notice savedNotice = captor.getValue();
        assertEquals(response.getId(), savedNotice.getId());
        assertEquals(request.getTitle(), savedNotice.getTitle());
        assertEquals(request.getContent(), savedNotice.getContent());
        assertEquals(request.getFileNameList(), savedNotice.getFileNameList() != null ? savedNotice.getFileNameList() : null);
    }
}