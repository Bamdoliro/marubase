package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import com.bamdoliro.maru.presentation.question.dto.request.CreateQuestionRequest;
import com.bamdoliro.maru.shared.fixture.QuestionFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateQuestionUseCaseTest {

    @InjectMocks
    private CreateQuestionUseCase createQuestionUseCase;

    @Mock
    private QuestionRepository questionRepository;

    @Test
    void 유저가_자주묻는질문을_생성한다() {
        // given
        Question question = QuestionFixture.createQuestion();
        CreateQuestionRequest request = new CreateQuestionRequest(question.getTitle(), question.getContent(), question.getCategory());
        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);

        given(questionRepository.save(any(Question.class))).willReturn(question);

        // when
        createQuestionUseCase.execute(request);

        // then
        verify(questionRepository, times(1)).save(captor.capture());
        Question savedQuestion = captor.getValue();
        assertEquals(request.getTitle(), savedQuestion.getTitle());
        assertEquals(request.getContent(), savedQuestion.getContent());
    }
}