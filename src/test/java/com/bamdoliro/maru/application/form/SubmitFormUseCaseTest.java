package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.service.FormFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubmitFormUseCaseTest {

    @InjectMocks
    private SubmitFormUseCase submitFormUseCase;

    @Mock
    private FormFacade formFacade;

    @Test
    void 원서를_제출한다() {

    }
}