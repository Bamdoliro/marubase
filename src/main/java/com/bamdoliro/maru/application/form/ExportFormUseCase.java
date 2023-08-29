package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.SubjectMap;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.Schedule;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@UseCase
public class ExportFormUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final MergePdfService mergePdfService;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        SubjectMap subjectMap = form.getGrade().getSubjectList().getSubjectMap();
        Map<String, Object> formMap = Map.of(
                "form", form,
                "grade21", subjectMap.getSubjectListOf(2, 1),
                "grade22", subjectMap.getSubjectListOf(2, 2),
                "grade31", subjectMap.getSubjectListOf(3, 1),
                "year", Schedule.getAdmissionYear()
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        getRequiredTemplates(form.getType())
                .stream()
                .map((t) -> processTemplateService.execute(t, formMap))
                .map(generatePdfService::execute)
                .forEach((s) -> mergePdfService.execute(pdfMerger, s));

        mergedDocument.close();
        pdfMerger.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private void validateFormStatus(Form form) {
        if (!(form.isSubmitted() || form.isRejected())) {
            throw new FormAlreadySubmittedException();
        }
    }

    private List<String> getRequiredTemplates(FormType formType) {
        if (formType.isRegular()) {
            return List.of(
                    Templates.FORM,
                    Templates.DOCUMENT,
                    Templates.NO_SMOKING
            );
        }

        return List.of(
                Templates.FORM,
                Templates.DOCUMENT,
                Templates.RECOMMENDATION,
                Templates.NO_SMOKING
        );
    }
}
