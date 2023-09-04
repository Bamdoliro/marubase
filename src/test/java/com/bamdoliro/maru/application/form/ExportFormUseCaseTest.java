package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.pdf.exception.FailedToExportPdfException;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.itextpdf.kernel.utils.PdfMerger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExportFormUseCaseTest {

    @InjectMocks
    private ExportFormUseCase exportFormUseCase;

    @Mock
    private FormFacade formFacade;

    @Mock
    private ProcessTemplateService processTemplateService;

    @Mock
    private GeneratePdfService generatePdfService;

    @Mock
    private MergePdfService mergePdfService;

    @Test
    void 일반전형_원서를_pdf로_다운받는다() {
        // given
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formFacade.getForm(user)).willReturn(form);
        given(processTemplateService.execute(any(String.class), any())).willReturn("html");
        given(generatePdfService.execute(any(String.class))).willReturn(new ByteArrayOutputStream());
        willDoNothing().given(mergePdfService).execute(any(PdfMerger.class), any(ByteArrayOutputStream.class));

        // when
        exportFormUseCase.execute(user);

        // then
        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, times(4)).execute(any(String.class), any());
        verify(generatePdfService, times(4)).execute(any(String.class));
        verify(mergePdfService, times(4)).execute(any(PdfMerger.class), any(ByteArrayOutputStream.class));
    }

    @Test
    void 일반전형이_아닌_원서를_pdf로_다운받는다() {
        // given
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.FROM_NORTH_KOREA);
        given(formFacade.getForm(user)).willReturn(form);
        given(processTemplateService.execute(any(String.class), any())).willReturn("html");
        given(generatePdfService.execute(any(String.class))).willReturn(new ByteArrayOutputStream());
        willDoNothing().given(mergePdfService).execute(any(PdfMerger.class), any(ByteArrayOutputStream.class));

        // when
        exportFormUseCase.execute(user);

        // then
        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, times(5)).execute(any(String.class), any());
        verify(generatePdfService, times(5)).execute(any(String.class));
        verify(mergePdfService, times(5)).execute(any(PdfMerger.class), any(ByteArrayOutputStream.class));
    }

    @Test
    void 원서를_pdf로_다운받을_때_이미_제출한_원서라면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);
        form.submit("https://maru.bamdoliro.com/form.pdf");
        given(formFacade.getForm(user)).willReturn(form);

        // when and then
        assertThrows(FormAlreadySubmittedException.class, () -> exportFormUseCase.execute(user));

        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, never()).execute(any(String.class), any());
        verify(generatePdfService, never()).execute(any(String.class));
        verify(mergePdfService, never()).execute(any(PdfMerger.class), any(ByteArrayOutputStream.class));
    }

    @Test
    void 원서를_pdf로_다운받을_때_모종의_이유로_변환_과정에서_실패하면_에러가_발생한다() {
        // given
        User user = UserFixture.createUser();
        Form form = FormFixture.createForm(FormType.REGULAR);
        given(formFacade.getForm(user)).willReturn(form);
        given(processTemplateService.execute(any(String.class), any())).willReturn("html");
        doThrow(FailedToExportPdfException.class).when(generatePdfService).execute(any(String.class));

        // when and then
        assertThrows(FailedToExportPdfException.class, () -> exportFormUseCase.execute(user));

        // then
        verify(formFacade, times(1)).getForm(user);
        verify(processTemplateService, times(1)).execute(any(String.class), any());
        verify(generatePdfService, times(1)).execute(any(String.class));
        verify(mergePdfService, never()).execute(any(PdfMerger.class), any(ByteArrayOutputStream.class));
    }
}