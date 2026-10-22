package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.ApplicationFormXlsxWriter;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExportApplicationFormUseCaseTest {

    @InjectMocks
    private ExportApplicationFormUseCase exportApplicationFormUseCase;

    @Mock
    private FormRepository formRepository;

    @Mock
    private ApplicationFormXlsxWriter applicationFormXlsxWriter;

    @Test
    void 지원자등록양식을_id_오름차순으로_내보낸다() throws Exception {
        // given
        given(formRepository.findByStatus(FormStatus.APPROVED))
                .willReturn(List.of(form(3L), form(1L), form(5L), form(2L), form(4L)));

        // when
        exportApplicationFormUseCase.execute();

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Form>> captor = ArgumentCaptor.forClass(List.class);
        verify(applicationFormXlsxWriter).write(captor.capture());
        assertThat(captor.getValue())
                .extracting(Form::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    private Form form(Long id) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "id", id);
        return form;
    }
}
