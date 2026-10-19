package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class DownloadAdmissionAndPledgeFormatUseCase {

    private final MergePdfService mergePdfService;
    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final AdmissionScheduleFacade admissionScheduleFacade;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validate(form);

        Map<String, Object> formMap = Map.of(
                "form", form,
                "year", admissionScheduleFacade.getCurrentSchedule().getAdmissionYear()
        );

        List<String> templates = List.of(
                Templates.REGISTRATION,
                Templates.NO_SMOKING
        );


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        templates
                .stream()
                .map((t) -> processTemplateService.execute(t, formMap))
                .map(generatePdfService::execute)
                .forEach((s) -> mergePdfService.execute(pdfMerger, s));

        mergedDocument.close();
        pdfMerger.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private void validate(Form form) {
        if (!form.isPassedNow() && !form.isEntered())
            throw new InvalidFormStatusException();
    }
}
