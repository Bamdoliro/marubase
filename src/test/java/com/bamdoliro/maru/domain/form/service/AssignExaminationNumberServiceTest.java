package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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
        given(formRepository.findMaxExaminationNumber(1000L, 2000L)).willReturn(Optional.of(1005L));

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(1006L, form.getExaminationNumber());
        verify(formRepository).findMaxExaminationNumber(1000L, 2000L);
    }

    @Test
    void 이전에_수험번호가_없다면_초기값을_부여한다() {
        // given
        Form form = FormFixture.createForm(FormType.TEEN_HOUSEHOLDER);
        given(formRepository.findMaxExaminationNumber(3000L, 4000L)).willReturn(Optional.empty());

        // when
        assignExaminationNumberService.execute(form);

        // then
        assertEquals(3001L, form.getExaminationNumber());
        verify(formRepository).findMaxExaminationNumber(3000L, 4000L);
    }
}