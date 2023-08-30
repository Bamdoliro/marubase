package com.bamdoliro.maru.application.question;

import com.bamdoliro.maru.infrastructure.persistence.question.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteQuestionUseCaseTest {

    @InjectMocks
    private DeleteQuestionUseCase deleteQuestionUseCase;

    @Mock
    private QuestionRepository questionRepository;

    @Test
    void 정상적으로_자주묻는질문을_삭제한다() {
        // given
        Long id = 1L;
        willDoNothing().given(questionRepository).deleteById(id);

        // when
        deleteQuestionUseCase.execute(id);

        // then
        verify(questionRepository, times(1)).deleteById(id);
    }
}