package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FormFacadeTest {

    @InjectMocks
    private FormFacade formFacade;

    @Mock
    private FormRepository formRepository;

    @Test
    void 원서를_가져온다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formRepository.findById(form.getId())).willReturn(Optional.of(form));

        // when
        Form foundForm = formFacade.getForm(form.getId());

        // then
        assertEquals(form.getId(), foundForm.getId());
    }

    @Test
    void 존재하지_않는_원서일_때_에러가_발생한다() {
        // given
        given(formRepository.findById(anyLong())).willReturn(Optional.empty());

        // when and then
        assertThrows(FormNotFoundException.class, () -> formFacade.getForm(415L));
    }
}