package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
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
class QuerySubmittedFormUseCaseTest {

    @InjectMocks
    private QuerySubmittedFormUseCase querySubmittedFormUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 검토해야_하는_원서를_가져온다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.REGULAR)
        );
        given(formRepository.findByStatus(FormStatus.FINAL_SUBMITTED)).willReturn(formList);

        // when
        List<FormSimpleResponse> formSimpleResponseList = querySubmittedFormUseCase.execute();

        // then
        verify(formRepository, times(1)).findByStatus(FormStatus.FINAL_SUBMITTED);
        assertEquals(formSimpleResponseList.size(), formList.size());
        assertEquals(formSimpleResponseList.get(0).getId(), formList.get(0).getId());
        assertEquals(formSimpleResponseList.get(1).getId(), formList.get(1).getId());
        assertEquals(formSimpleResponseList.get(2).getId(), formList.get(2).getId());
    }

    @Test
    void 검토해야_하는_원서가_없으면_빈_리스트를_반환한다() {
        // given
        given(formRepository.findByStatus(FormStatus.FINAL_SUBMITTED)).willReturn(List.of());

        // when
        List<FormSimpleResponse> formSimpleResponseList = querySubmittedFormUseCase.execute();

        // then
        verify(formRepository, times(1)).findByStatus(FormStatus.FINAL_SUBMITTED);
        assertEquals(formSimpleResponseList.size(), 0);
    }

}