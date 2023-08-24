package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import com.bamdoliro.maru.domain.form.exception.DraftFormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.DraftFormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.fixture.DraftFormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryDraftFormUseCaseTest {

    @InjectMocks
    private QueryDraftFormUseCase queryDraftFormUseCase;

    @Mock
    private DraftFormRepository draftFormRepository;

    @Test
    void 임시저장된_원서를_조회한다() {
        // given
        User user = UserFixture.createUser();
        DraftForm draftForm = DraftFormFixture.createDraftForm();
        given(draftFormRepository.findById(user.getPhoneNumber())).willReturn(Optional.of(draftForm));

        // when
        SubmitFormRequest response = queryDraftFormUseCase.execute(user);

        // then
        assertEquals(draftForm.getSubmitFormRequest(), response);
        verify(draftFormRepository, times(1)).findById(user.getPhoneNumber());
    }

    @Test
    void 임시저장된_원서를_조회할_때_임시저장된_원서가_없으면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        given(draftFormRepository.findById(user.getPhoneNumber())).willReturn(Optional.empty());

        // when and then
        assertThrows(DraftFormNotFoundException.class,
                () -> queryDraftFormUseCase.execute(user));

        verify(draftFormRepository, times(1)).findById(user.getPhoneNumber());
    }
}
