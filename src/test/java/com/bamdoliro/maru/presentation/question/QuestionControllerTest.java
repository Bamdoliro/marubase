package com.bamdoliro.maru.presentation.question;

import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuestionControllerTest extends RestDocsTestSupport {

    @Test
    void 자주묻는질문을_불러온다() throws Exception {
        List<QuestionResponse> response = List.of(
                QuestionFixture.createQuestionResponse(),
                QuestionFixture.createQuestionResponse(),
                QuestionFixture.createQuestionResponse()
        );
        given(queryQuestionListUseCase.execute(QuestionCategory.TOP_QUESTION)).willReturn(response);

        mockMvc.perform(get("/questions")
                        .param("category", QuestionCategory.TOP_QUESTION.name())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("category")
                                        .optional()
                                        .description("<<question-category,카테고리>>")
                        )
                ));

        verify(queryQuestionListUseCase, times(1)).execute(QuestionCategory.TOP_QUESTION);
    }

    @Test
    void 자주묻는질문을_id로_불러온다() throws Exception {
        Long id = 1L;
        given(queryQuestionUseCase.execute(id)).willReturn(QuestionFixture.createQuestionResponse());

        mockMvc.perform(get("/questions/{question-id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("question-id")
                                        .description("자주 묻는 질문 id")
                        )
                ));

        verify(queryQuestionUseCase, times(1)).execute(id);
    }

    @Test
    void 자주묻는질문을_id로_불러올_때_자주묻는질문이_없으면_에러가_발생한다() throws Exception {
        Long id = -1L;
        given(queryQuestionUseCase.execute(id)).willThrow(new QuestionNotFoundException());

        mockMvc.perform(get("/questions/{question-id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }


}
