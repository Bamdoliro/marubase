package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.presentation.question.dto.response.QuestionResponse;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryQuestionUseCaseTest {

    @InjectMocks
    private QueryQuestionUseCase queryQuestionUseCase;

    @Mock
    private QuestionFacade questionFacade;

    @Test
    void 자주묻는질문을_가져온다() {
        // given
        Question question = QuestionFixture.createQuestion();
        given(questionFacade.getQuestion(question.getId())).willReturn(question);

        // when
        QuestionResponse response = queryQuestionUseCase.execute(question.getId());

        // then
        assertEquals(question.getTitle(), response.getTitle());
        assertEquals(question.getContent(), response.getContent());
        assertEquals(question.getCategory(), response.getCategory());

        verify(questionFacade, times(1)).getQuestion(question.getId());
    }

    @Test
    void 자주묻는질문을_가져올_때_없으면_에러가_발생한다() {
        // given
        Long questionId = -1L;
        willThrow(new QuestionNotFoundException()).given(questionFacade).getQuestion(questionId);

        // when and then
        assertThrows(QuestionNotFoundException.class,
                () -> queryQuestionUseCase.execute(questionId));

        verify(questionFacade, times(1)).getQuestion(questionId);
    }
}