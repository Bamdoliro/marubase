package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.DraftFormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.shared.fixture.DraftFormFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DraftFormUseCaseTest {

    @InjectMocks
    private DraftFormUseCase draftFormUseCase;

    @Mock
    private DraftFormRepository draftFormRepository;

    @Test
    void 원서를_임시저장한다() {
        // given
        User user = UserFixture.createUser();
        SubmitFormRequest request = FormFixture.createFormRequest(FormType.REGULAR);
        given(draftFormRepository.save(any(DraftForm.class))).willReturn(DraftFormFixture.createDraftForm());

        // when
        draftFormUseCase.execute(user, request);

        // then
        verify(draftFormRepository, times(1)).save(any(DraftForm.class));
    }
}