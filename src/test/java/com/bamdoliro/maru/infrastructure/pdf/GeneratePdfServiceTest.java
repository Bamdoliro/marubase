package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.FormService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
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

    @Test
    void 파일을_저장한다() throws IOException {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);
        formService.calculateScore(form);
        String html = processTemplateService.execute("form", Map.of(
                "form", form
        ));

        // when
        ByteArrayOutputStream stream = generatePdfService.execute(html);
        FileOutputStream fileOutputStream = new FileOutputStream("src/test/resources/test.pdf");
        stream.writeTo(fileOutputStream);
        stream.close();
        fileOutputStream.close();
    }
}