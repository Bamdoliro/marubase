package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.exception.QuestionNotFoundException;
import com.bamdoliro.maru.domain.user.exception.UserNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionUseCaseTest {

    @InjectMocks
    private UpdateQuestionUseCase updateQuestionUseCase;

    @Mock
    private QuestionFacade questionFacade;


    @Test
    void 유저가_자주묻는질문을_수정한다() {
        //given
        Question question = QuestionFixture.createQuestion();
        UpdateQuestionRequest request = new UpdateQuestionRequest("제목 바뀌나?", "내용도 바뀌나?");

        given(questionFacade.getQuestion(question.getId())).willReturn(question);

        //when and then
        updateQuestionUseCase.execute(question.getId(), request);
        verify(questionFacade, times(1)).getQuestion(question.getId());
        assertEquals(request.getTitle(), question.getTitle());
        assertEquals(request.getContent(), question.getContent());
    }

    @Test
    void 유저가_자주묻는질문을_수정할_때_자주묻는질문이_없으면_에러가_발생한다() {
        // given
        Question question = QuestionFixture.createQuestion();
        UpdateQuestionRequest request = new UpdateQuestionRequest("제목 바뀌나?", "내용도 바뀌나?");

        given(questionFacade.getQuestion(question.getId())).willThrow(QuestionNotFoundException.class);

        // when and then
        assertThrows(QuestionNotFoundException.class, () -> updateQuestionUseCase.execute(question.getId(), request));

        verify(questionFacade, times(1)).getQuestion(question.getId());
        assertNotEquals(request.getTitle(), question.getTitle());
        assertNotEquals(request.getContent(), question.getContent());
    }
}
