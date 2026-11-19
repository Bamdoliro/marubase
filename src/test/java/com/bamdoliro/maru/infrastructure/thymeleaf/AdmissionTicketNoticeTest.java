package com.bamdoliro.maru.infrastructure.thymeleaf;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.policy.TicketNotice;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.assertj.core.api.Assertions.assertThat;

class AdmissionTicketNoticeTest {
    @Test
    void 관리자_안내문을_실제_PDF에_반영하고_HTML은_이스케이프한다() throws Exception {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        ProcessTemplateService processor = new ProcessTemplateService(engine);
        Map<String, Object> values = new HashMap<>();
        values.put("form", FormFixture.createForm(FormType.REGULAR));
        values.put("year", 2027);
        for (String field : new String[]{"codingTest", "ncs", "depthInterview", "physicalExamination",
                "announcementOfSecondPass", "meisterTalentEntranceTime", "meisterTalentExclusionEntranceTime",
                "entranceRegistrationTime"}) {
            values.put(field, "2026. 10. 30. 09:30");
        }
        values.put("identificationPictureUri", new ClassPathResource("images/id-picture.png").getURL().toExternalForm());
        TicketNotice custom = new TicketNotice("신관 평가장", "입학 홈페이지", "행정실",
                "예비소집 별도 안내", "등기 또는 방문", "점심은 도시락을 지참해주세요.",
                "수험표와 필기도구", "아날로그 시계", "휴대전화 및 통신기기");
        for (TicketNotice notice : new TicketNotice[]{TicketNotice.defaults(), custom}) {
            values.put("notice", notice);
            String html = processor.execute("admission-ticket", values);
            byte[] pdf = new GeneratePdfService().execute(html).toByteArray();
            try (PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)))) {
                assertThat(document.getNumberOfPages()).isEqualTo(1);
                String text = PdfTextExtractor.getTextFromPage(document.getFirstPage()).replaceAll("\\s+", "");
                assertThat(text).contains(notice.examinationLocation().replace(" ", ""))
                        .contains(notice.lunchNotice().replace(" ", ""));
            }
            Path directory = Path.of("build", "policy-pdfs");
            Files.createDirectories(directory);
            Files.write(directory.resolve(notice == custom ? "ticket-custom.pdf" : "ticket-default.pdf"), pdf);
        }
        values.put("notice", new TicketNotice("<b>평가장</b>", "홈페이지", "행정실",
                "안내", "방문", "도시락", "수험표", "시계", "전화기"));
        assertThat(processor.execute("admission-ticket", values))
                .contains("&lt;b&gt;평가장&lt;/b&gt;").doesNotContain("<b>평가장</b>");
    }
}
