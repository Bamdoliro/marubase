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

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
    void 승인_1차합격_1차불합격_상태로만_조회한다() throws Exception {
        // given
        given(formRepository.findByStatusIn(anyCollection())).willReturn(List.of());

        // when
        exportApplicationFormUseCase.execute();

        // then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<FormStatus>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(formRepository).findByStatusIn(captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder(
                FormStatus.APPROVED,
                FormStatus.FIRST_PASSED,
                FormStatus.FIRST_FAILED
        );
        verify(formRepository, never()).findByStatus(any());
    }

    @Test
    void 승인_1차합격_1차불합격_원서를_내보낸다() throws Exception {
        // given
        Form approved = form(1L, FormStatus.APPROVED);
        Form firstPassed = form(2L, FormStatus.FIRST_PASSED);
        Form firstFailed = form(3L, FormStatus.FIRST_FAILED);
        given(formRepository.findByStatusIn(anyCollection()))
                .willReturn(List.of(approved, firstPassed, firstFailed));

        // when
        exportApplicationFormUseCase.execute();

        // then
        assertThat(captureWrittenFormList()).containsExactly(approved, firstPassed, firstFailed);
    }

    @Test
    void 지원자등록양식을_id_오름차순으로_내보낸다() throws Exception {
        // given
        given(formRepository.findByStatusIn(anyCollection()))
                .willReturn(List.of(
                        form(3L, FormStatus.APPROVED),
                        form(1L, FormStatus.FIRST_FAILED),
                        form(5L, FormStatus.FIRST_PASSED),
                        form(2L, FormStatus.APPROVED),
                        form(4L, FormStatus.FIRST_PASSED)
                ));

        // when
        exportApplicationFormUseCase.execute();

        // then
        assertThat(captureWrittenFormList())
                .extracting(Form::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    private List<Form> captureWrittenFormList() throws Exception {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Form>> captor = ArgumentCaptor.forClass(List.class);
        verify(applicationFormXlsxWriter).write(captor.capture());
        return captor.getValue();
    }

    private Form form(Long id, FormStatus status) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "id", id);
        ReflectionTestUtils.setField(form, "status", status);
        return form;
    }
}
