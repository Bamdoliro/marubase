package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.FormService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Disabled
@SpringBootTest
class GeneratePdfServiceTest {

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private FormService formService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Autowired
    private MergePdfService mergePdfService;

    @Test
    void 파일을_저장한다() throws IOException {
        // given
        Form form = FormFixture.createForm(FormType.MEISTER_TALENT);
        formService.calculateScore(form);
        Map<String, Object> formMap = Map.of("form", form);

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
        FileOutputStream fileOutputStream = new FileOutputStream("src/test/resources/test.pdf");
        outputStream.writeTo(fileOutputStream);
        outputStream.close();
        fileOutputStream.close();
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