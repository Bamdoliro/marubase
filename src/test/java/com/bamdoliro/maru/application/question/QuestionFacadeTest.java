package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.application.question.QuestionFacade;
import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
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
class QuestionFacadeTest {

    @InjectMocks
    private QuestionFacade questionFacade;

    @Mock
    private QuestionRepository questionRepository;

    @Test
    void 자주묻는질문을_가져온다() {
        // given
        Question question = QuestionFixture.createQuestion();
        given(questionRepository.findById(question.getId())).willReturn(Optional.of(question));

        // when
        Question foundQuestion = questionFacade.getQuestion(question.getId());

        // then
        assertEquals(question.getId(), foundQuestion.getId());
    }

    @Test
    void 존재하지_않는_자주묻는질문일_때_에러가_발생한다() {
        // given
        given(questionRepository.findById(anyLong())).willReturn(Optional.empty());

        // when and then
        assertThrows(QuestionNotFoundException.class, () -> questionFacade.getQuestion(-1L));
    }
}