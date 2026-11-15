package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.ExaminationNumberRangeExceededException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignExaminationNumberServiceTest {

    @InjectMocks
    private AssignExaminationNumberService assignExaminationNumberService;

    @Mock
    private FormRepository formRepository;

    @Test
    void 수험번호를_부여한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1001L, 1002L, 1003L, 1004L, 1005L));

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(1006L, form.getExaminationNumber());
        verify(formRepository).findAllExaminationNumber();
    }

    @Test
    void 이전에_수험번호가_없다면_초기값을_부여한다() {
        // given
        Form form = FormFixture.createForm(FormType.TEEN_HOUSEHOLDER);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of());

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(3001L, form.getExaminationNumber());
        verify(formRepository).findAllExaminationNumber();
    }

    @Test
    void 여러_원서에_한_번에_수험번호를_부여한다() {
        // given
        Form regular1 = FormFixture.createForm(FormType.REGULAR);
        Form social = FormFixture.createForm(FormType.ONE_PARENT);
        Form regular2 = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1001L, 1002L, 3001L));

        // when
        assignExaminationNumberService.executeAll(List.of(regular1, social, regular2));

        // then
        assertEquals(1003L, regular1.getExaminationNumber());
        assertEquals(3002L, social.getExaminationNumber());
        assertEquals(1004L, regular2.getExaminationNumber());
        verify(formRepository, times(1)).findAllExaminationNumber();
    }

    @Test
    void 여러_원서에_수험번호를_부여할_때_전형_구간을_넘으면_에러가_발생한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findAllExaminationNumber()).willReturn(List.of(1999L));

        // when and then
        assertThrows(ExaminationNumberRangeExceededException.class,
                () -> assignExaminationNumberService.executeAll(List.of(form)));
    }
}
