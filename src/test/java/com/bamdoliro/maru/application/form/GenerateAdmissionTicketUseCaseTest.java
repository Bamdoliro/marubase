package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenerateAdmissionTicketUseCaseTest {

    @InjectMocks
    private GenerateAdmissionTicketUseCase generateAdmissionTicketUseCase;

    @Mock
    private FormFacade formFacade;

    @Mock
    private ProcessTemplateService processTemplateService;

    @Mock
    private GeneratePdfService generatePdfService;

    @Mock
    private FileService fileService;

    @Test
    void 수험표를_생성한다() {
        // given
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.MULTI_CHILDREN);
        form.firstPass();
        given(formFacade.getForm(user)).willReturn(form);
        given(processTemplateService.execute(any(String.class), any(Map.class))).willReturn("html");
        given(fileService.getPresignedUrl(any(String.class), any(String.class))).willReturn(SharedFixture.createFormUrlResponse());
        given(generatePdfService.execute(any(String.class))).willReturn(new ByteArrayOutputStream());

        // when
        generateAdmissionTicketUseCase.execute(user);

        // then
        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, times(1)).execute(any(String.class), any(Map.class));
        verify(generatePdfService, times(1)).execute(any(String.class));
        verify(fileService, times(1)).getPresignedUrl(any(String.class), any(String.class));
    }

    @Test
    void 수험표를_생성할_때_불합격한_사용자라면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.MULTI_CHILDREN);
        given(formFacade.getForm(user)).willReturn(form);

        // when and then
        assertThrows(InvalidFormStatusException.class, () -> generateAdmissionTicketUseCase.execute(user));

        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, never()).execute(any(String.class), any(Map.class));
        verify(generatePdfService, never()).execute(any(String.class));
    }

    @Test
    void 수험표를_생성할_때_원서를_접수하지_않았다면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        willThrow(new FormNotFoundException()).given(formFacade).getForm(user);

        // when and then
        assertThrows(FormNotFoundException.class, () -> generateAdmissionTicketUseCase.execute(user));

        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, never()).execute(any(String.class), any(Map.class));
        verify(generatePdfService, never()).execute(any(String.class));
    }
}