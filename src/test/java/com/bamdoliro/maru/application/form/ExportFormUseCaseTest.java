package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExportFormUseCaseTest {

    @Autowired
    private FormFacade formFacade;

    @Autowired
    private ProcessTemplateService processTemplateService;

    @Autowired
    private GeneratePdfService generatePdfService;

    @Autowired
    private MergePdfService mergePdfService;



}