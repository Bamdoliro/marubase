package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteNoticeUseCaseTest {

    @InjectMocks
    private DeleteNoticeUseCase deleteNoticeUseCase;

    @Mock
    private NoticeRepository noticeRepository;

    @Test
    void 정상적으로_공지사항을_삭제한다() {
        // given
        Long id = 1L;
        willDoNothing().given(noticeRepository).deleteById(id);

        // when
        deleteNoticeUseCase.execute(id);

        // then
        verify(noticeRepository, times(1)).deleteById(id);
    }
}