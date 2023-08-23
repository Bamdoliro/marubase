package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.domain.notice.exception.NoticeNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NoticeFacadeTest {

    @InjectMocks
    private NoticeFacade noticeFacade;

    @Mock
    private NoticeRepository noticeRepository;

    @Test
    void 공지를_가져온다() {
        // given
        Notice notice = NoticeFixture.createNotice();
        given(noticeRepository.findById(notice.getId())).willReturn(Optional.of(notice));

        // when
        Notice foundNotice = noticeFacade.getNotice(notice.getId());

        // then
        assertEquals(notice.getId(), foundNotice.getId());
    }

    @Test
    void 존재하지_않는_공지일_때_에러가_발생한다() {
        // given
        given(noticeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when and then
        assertThrows(NoticeNotFoundException.class, () -> noticeFacade.getNotice(-1L));
    }
}