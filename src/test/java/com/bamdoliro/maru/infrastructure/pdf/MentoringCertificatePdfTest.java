package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MentoringCertificatePdfTest {

    @Test
    void 이수증_첨부_안내만_있는_A4_한_페이지를_생성한다() throws Exception {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        String html = new ProcessTemplateService(engine).execute(Templates.MENTORING_CERTIFICATE, Map.of());
        byte[] pdf = new GeneratePdfService().execute(html).toByteArray();

        try (PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)))) {
            assertThat(document.getNumberOfPages()).isEqualTo(1);
            assertThat(PdfTextExtractor.getTextFromPage(document.getPage(1)).trim())
                    .isEqualTo("멘토멘티 프로그램 이수증을 첨부해주십시오");
            assertThat(document.getPage(1).getPageSize().getWidth()).isBetween(594f, 596f);
            assertThat(document.getPage(1).getPageSize().getHeight()).isBetween(841f, 843f);
        }
        Path preview = Path.of("build", "mentoring-certificate-preview.pdf");
        Files.createDirectories(preview.getParent());
        Files.write(preview, pdf);
    }
}
