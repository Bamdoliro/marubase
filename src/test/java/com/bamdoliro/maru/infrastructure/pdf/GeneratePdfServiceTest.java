package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.lowagie.text.DocumentException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
class GeneratePdfServiceTest {

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Test
    void 파일을_저장한다() throws DocumentException, IOException {
        // given
        String html = processTemplateService.execute("no-smoking", null);

        // when
        generatePdfService.execute(html);
    }
}