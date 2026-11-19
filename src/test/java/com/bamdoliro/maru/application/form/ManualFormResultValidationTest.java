package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormResultRequestException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ManualFormResultValidationTest {

    @Mock private FormRepository formRepository;
    @Mock private AdmissionScheduleFacade admissionScheduleFacade;
    @InjectMocks private PassOrFailFormUseCase useCase;

    @ParameterizedTest
    @EnumSource(value = FormStatus.class, names = {"FIRST_PASSED", "PASSED", "FAILED"}, mode = EnumSource.Mode.EXCLUDE)
    void 허용되지_않은_상태가_섞이면_전체_거부한다(FormStatus status) {
        Form valid = form(1L, FormStatus.FIRST_PASSED);
        Form invalid = form(2L, status);
        given(formRepository.findAllByIdsForUpdate(anyList())).willReturn(List.of(valid, invalid));
        assertThatThrownBy(() -> useCase.execute(request(true, true)))
                .isInstanceOf(InvalidFormStatusException.class);
        assertThat(valid.getStatus()).isEqualTo(FormStatus.FIRST_PASSED);
        assertThat(invalid.getStatus()).isEqualTo(status);
    }

    @ParameterizedTest
    @EnumSource(value = FormStatus.class, names = {"FIRST_PASSED", "PASSED", "FAILED"})
    void 일차합격과_최종결과만_합격_불합격으로_변경한다(FormStatus status) {
        Form first = form(1L, status);
        Form second = form(2L, status);
        given(formRepository.findAllByIdsForUpdate(anyList())).willReturn(List.of(second, first));
        useCase.execute(request(true, false));
        assertThat(first.getStatus()).isEqualTo(FormStatus.PASSED);
        assertThat(second.getStatus()).isEqualTo(FormStatus.FAILED);
    }

    @Test
    void 중복_ID는_조회하기_전에_거부한다() {
        assertThatThrownBy(() -> useCase.execute(new PassOrFailFormListRequest(List.of(
                new PassOrFailFormRequest(1L, true), new PassOrFailFormRequest(1L, false)))))
                .isInstanceOf(InvalidFormResultRequestException.class);
        verifyNoInteractions(formRepository, admissionScheduleFacade);
    }

    @Test
    void 합격여부_누락을_불합격으로_처리하지_않는다() {
        assertThatThrownBy(() -> useCase.execute(new PassOrFailFormListRequest(List.of(
                new PassOrFailFormRequest(1L, null)))))
                .isInstanceOf(InvalidFormResultRequestException.class);
        verifyNoInteractions(formRepository, admissionScheduleFacade);
    }

    @Test
    void 존재하지_않는_ID가_섞이면_전체_거부한다() {
        Form valid = form(1L, FormStatus.FIRST_PASSED);
        given(formRepository.findAllByIdsForUpdate(anyList())).willReturn(List.of(valid));
        assertThatThrownBy(() -> useCase.execute(request(true, false)))
                .isInstanceOf(FormNotFoundException.class);
        assertThat(valid.getStatus()).isEqualTo(FormStatus.FIRST_PASSED);
    }

    private Form form(Long id, FormStatus status) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "id", id);
        ReflectionTestUtils.setField(form, "status", status);
        return form;
    }

    private PassOrFailFormListRequest request(boolean first, boolean second) {
        return new PassOrFailFormListRequest(List.of(
                new PassOrFailFormRequest(2L, second), new PassOrFailFormRequest(1L, first)));
    }
}
