package com.bamdoliro.maru.infrastructure.thymeleaf;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.SubjectMap;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.assertj.core.api.Assertions.assertThat;

class AdmissionDocumentYearTest {

    @ParameterizedTest
    @ValueSource(strings = {"form", "qualification-form", "special-admission", "registration"})
    void 제목과_본문의_학년도가_전달된_일정에_따라_변경된다(String template) throws Exception {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        ProcessTemplateService processor = new ProcessTemplateService(engine);
        Form form = template.equals("qualification-form")
                ? FormFixture.createQualificationExaminationForm(FormType.REGULAR)
                : FormFixture.createForm(FormType.REGULAR);
        form.updateScore(new Score(100.0, 10, 10, 0));
        SubjectMap subjects = form.getGrade().getSubjectList().getSubjectMap();

        // Reuse the same template engine to catch accidentally cached year values.
        for (int year : new int[]{2027, 2031}) {
            String html = processor.execute(template, Map.of(
                    "form", form,
                    "year", year,
                    "grade11", subjects.getSubjectListOf(1, 1),
                    "grade12", subjects.getSubjectListOf(1, 2),
                    "grade21", subjects.getSubjectListOf(2, 1),
                    "grade22", subjects.getSubjectListOf(2, 2),
                    "grade31", subjects.getSubjectListOf(3, 1),
                    "identificationPictureUri",
                    new ClassPathResource("images/id-picture.png").getURL().toExternalForm()
            ));
            byte[] pdf = new GeneratePdfService().execute(html).toByteArray();
            try (PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)))) {
                StringBuilder text = new StringBuilder();
                for (int page = 1; page <= document.getNumberOfPages(); page++) {
                    text.append(PdfTextExtractor.getTextFromPage(document.getPage(page)));
                }
                String content = text.toString().replaceAll("\\s+", "");
                assertThat(content).contains(year + "학년도").doesNotContain("2025학년도");
                assertThat(content.split(year + "학년도", -1).length - 1).isGreaterThanOrEqualTo(2);
                if (year == 2031) {
                    assertThat(content).doesNotContain("2027학년도");
                }
            }
            if (year == 2031) {
                Path directory = Path.of("build", "schedule-pdfs");
                Files.createDirectories(directory);
                Files.write(directory.resolve(template + ".pdf"), pdf);
            }
        }
    }
}
