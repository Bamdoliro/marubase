package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryNoticeUseCaseTest {

    @InjectMocks
    private QueryNoticeUseCase queryNoticeUseCase;

    @Mock
    private NoticeFacade noticeFacade;

    @Test
    void 공지사항을_불러온다() {
        // given
        Notice notice = NoticeFixture.createNotice();
        given(noticeFacade.getNotice(notice.getId())).willReturn(notice);

        // when
        NoticeResponse response = queryNoticeUseCase.execute(notice.getId());

        // then
        assertEquals(notice.getTitle(), response.getTitle());
        assertEquals(notice.getContent(), response.getContent());
        verify(noticeFacade, times(1)).getNotice(notice.getId());
    }
}