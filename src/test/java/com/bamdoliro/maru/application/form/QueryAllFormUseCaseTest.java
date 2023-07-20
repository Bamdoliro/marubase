package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
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
class QueryAllFormUseCaseTest {

    @InjectMocks
    private QueryAllFormUseCase queryAllFormUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 모든_원서를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, null);

        // then
        assertEquals(formList.size(), returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 특별전형_원서만_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, FormType.Category.SPECIAL);

        // then
        assertEquals(2, returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 사회통합전형_원서만_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, FormType.Category.SOCIAL_INTEGRATION);

        // then
        assertEquals(1, returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }
}