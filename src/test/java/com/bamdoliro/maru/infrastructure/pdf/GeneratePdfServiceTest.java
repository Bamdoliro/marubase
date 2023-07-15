package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Disabled
@SpringBootTest
class GeneratePdfServiceTest {

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Test
    void 파일을_저장한다() throws IOException {
        // given√
        String html = processTemplateService.execute("document", null);

        // when
        ByteArrayOutputStream stream = generatePdfService.execute(html);
        FileOutputStream fileOutputStream = new FileOutputStream("src/test/resources/test.pdf");
        stream.writeTo(fileOutputStream);
        stream.close();
        fileOutputStream.close();
    }
}