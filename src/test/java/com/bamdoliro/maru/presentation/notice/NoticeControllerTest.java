package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.domain.notice.exception.NoticeNotFoundException;
import com.bamdoliro.maru.presentation.notice.dto.response.DownloadFileResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.fixture.NoticeFixture;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeControllerTest extends RestDocsTestSupport {

    @Test
    void 전체_공지사항을_불러온다() throws Exception {
        List<NoticeSimpleResponse> response = List.of(
                NoticeFixture.createNoticeSimpleResponse(),
                NoticeFixture.createNoticeSimpleResponse(),
                NoticeFixture.createNoticeSimpleResponse()
        );
        given(queryNoticeListUseCase.execute()).willReturn(response);

        mockMvc.perform(get("/notices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document());

        verify(queryNoticeListUseCase, times(1)).execute();
    }

    @Test
    void 공지사항을_불러온다() throws Exception {
        Long id = 1L;
        NoticeResponse response = new NoticeResponse(NoticeFixture.createNotice(), List.of(
                new DownloadFileResponse(
                        SharedFixture.createNoticeFileUrlResponse().getDownloadUrl(),
                        "notice-file.pdf"
                ),
                new DownloadFileResponse(
                        SharedFixture.createNoticeFileUrlResponse().getDownloadUrl(),
                        "notice-file.pdf"
                )
        ));
        given(queryNoticeUseCase.execute(id)).willReturn(response);

        mockMvc.perform(get("/notices/{notice-id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("notice-id")
                                        .description("공지사항 id")
                        )
                ));

        verify(queryNoticeUseCase, times(1)).execute(id);
    }

    @Test
    void 공지사항을_불러올_때_공지사항이_없으면_에러가_발생한다() throws Exception {
        Long id = 1L;
        willThrow(new NoticeNotFoundException()).given(queryNoticeUseCase).execute(id);

        mockMvc.perform(get("/notices/{notice-id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(queryNoticeUseCase, times(1)).execute(id);
    }

}