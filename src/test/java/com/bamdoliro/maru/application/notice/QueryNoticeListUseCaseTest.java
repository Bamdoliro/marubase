package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.infrastructure.persistence.notice.NoticeRepository;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryNoticeListUseCaseTest {

    @InjectMocks
    private QueryNoticeListUseCase queryNoticeListUseCase;

    @Mock
    private NoticeRepository noticeRepository;

    @Test
    void 전체_공지사항을_불러온다() {
        // given
        List<Notice> noticeList = List.of(
                NoticeFixture.createNotice(),
                NoticeFixture.createNotice(),
                NoticeFixture.createNotice()
        );
        given(noticeRepository.findAll()).willReturn(noticeList);

        // when
        List<NoticeSimpleResponse> response = queryNoticeListUseCase.execute();

        // then
        assertEquals(noticeList.size(), response.size());
        verify(noticeRepository, times(1)).findAll();
    }
}