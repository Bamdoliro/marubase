package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.domain.notice.exception.NoticeNotFoundException;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateNoticeUseCaseTest {

    @InjectMocks
    private UpdateNoticeUseCase updateNoticeUseCase;

    @Mock
    private NoticeFacade noticeFacade;


    @Test
    void 유저가_공지사항을_수정한다() {
        //given
        Notice notice = NoticeFixture.createNotice();
        NoticeRequest request = new NoticeRequest("제목 바뀌나?", "내용도 바뀌나?", null);

        given(noticeFacade.getNotice(notice.getId())).willReturn(notice);

        //when
        updateNoticeUseCase.execute(notice.getId(), request);

        // then
        verify(noticeFacade, times(1)).getNotice(notice.getId());
        assertEquals(request.getTitle(), notice.getTitle());
        assertEquals(request.getContent(), notice.getContent());
    }

    @Test
    void 유저가_공지사항을_수정할_때_공지사항이_없으면_에러가_발생한다() {
        // given
        Notice notice = NoticeFixture.createNotice();
        NoticeRequest request = new NoticeRequest("제목 바뀌나?", "내용도 바뀌나?", null);

        given(noticeFacade.getNotice(notice.getId())).willThrow(NoticeNotFoundException.class);

        // when and then
        assertThrows(NoticeNotFoundException.class, () -> updateNoticeUseCase.execute(notice.getId(), request));

        verify(noticeFacade, times(1)).getNotice(notice.getId());
        assertNotEquals(request.getTitle(), notice.getTitle());
        assertNotEquals(request.getContent(), notice.getContent());
    }
}
