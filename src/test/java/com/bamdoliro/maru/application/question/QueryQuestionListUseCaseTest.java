package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
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
class QueryQuestionListUseCaseTest {

    @InjectMocks
    private QueryQuestionListUseCase queryQuestionListUseCase;

    @Mock
    private QuestionRepository questionRepository;

    @Test
    void 자주묻는질문을_불러온다() {
        // given
        List<Question> questionList = List.of(
                QuestionFixture.createQuestion(),
                QuestionFixture.createQuestion(),
                QuestionFixture.createQuestion()
        );
        given(questionRepository.findByCategory(QuestionCategory.TOP_QUESTION)).willReturn(questionList);

        // when
        List<QuestionResponse> response = queryQuestionListUseCase.execute(QuestionCategory.TOP_QUESTION);

        // then
        assertEquals(questionList.size(), response.size());
        verify(questionRepository, times(1)).findByCategory(QuestionCategory.TOP_QUESTION);
    }
}