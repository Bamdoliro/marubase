package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.constants.Schedule;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.util.SaveFileUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Disabled
@SpringBootTest
class GeneratePdfServiceTest {

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Autowired
    private MergePdfService mergePdfService;

    @Test
    void 수험표를_저장한다() throws IOException {
        // given
        Form form = FormFixture.createForm(FormType.MULTI_CHILDREN);
        calculateFormScoreService.execute(form);
        form.assignExaminationNumber(2004L);
        Map<String, Object> formMap = Map.of(
                "form", form
        );
        String template = processTemplateService.execute("admission-ticket", formMap);
        ByteArrayOutputStream outputStream = generatePdfService.execute(template);

        SaveFileUtil.execute(outputStream, SaveFileUtil.PDF);
    }

    @Test
    void 파일을_저장한다() throws IOException {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        calculateFormScoreService.execute(form);
        form.assignExaminationNumber(2004L);
        Map<String, Object> formMap = Map.of(
                "form", form,
                "grade21", form.getGrade().getSubjectList().getSubjectMap().getSubjectListOf(2, 1),
                "grade22", form.getGrade().getSubjectList().getSubjectMap().getSubjectListOf(2, 2),
                "grade31", form.getGrade().getSubjectList().getSubjectMap().getSubjectListOf(3, 1),
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

        // when
        SaveFileUtil.execute(outputStream, SaveFileUtil.PDF);
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