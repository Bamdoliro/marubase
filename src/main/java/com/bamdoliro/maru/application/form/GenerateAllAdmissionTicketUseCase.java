package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
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
public class GenerateAllAdmissionTicketUseCase {

    private final FormRepository formRepository;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final MergePdfService mergePdfService;
    private final FileService fileService;

    public ByteArrayResource execute() {
        List<Form> formList = formRepository.findByStatus(FormStatus.FIRST_PASSED);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        formList.forEach(form -> mergePdfService.execute(pdfMerger, generateAdmissionTicket(form)));

        mergedDocument.close();
        pdfMerger.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private ByteArrayOutputStream generateAdmissionTicket(Form form) {

        Map<String, Object> formMap = Map.ofEntries(
                Map.entry("form", form),
                Map.entry("year", Schedule.getAdmissionYear()),
                Map.entry("codingTest", Schedule.toLocaleString(Schedule.CODING_TEST)),
                Map.entry("ncs", Schedule.toLocaleString(Schedule.NCS)),
                Map.entry("depthInterview", Schedule.toLocaleString(Schedule.DEPTH_INTERVIEW)),
                Map.entry("physicalExamination", Schedule.toLocaleString(Schedule.PHYSICAL_EXAMINATION)),
                Map.entry("announcementOfSecondPass", Schedule.toLocaleString(Schedule.ANNOUNCEMENT_OF_SECOND_PASS)),
                Map.entry("meisterTalentEntranceTime", Schedule.toLocaleString(Schedule.MEISTER_TALENT_ENTRANCE_TIME)),
                Map.entry("meisterTalentExclusionEntranceTime", Schedule.toLocaleString(Schedule.MEISTER_TALENT_EXCLUSION_ENTRANCE_TIME)),
                Map.entry("entranceRegistrationTime", Schedule.toLocaleString(Schedule.ENTRANCE_REGISTRATION_PERIOD_START, Schedule.ENTRANCE_REGISTRATION_PERIOD_END)),
                Map.entry("identificationPictureUri", fileService.getDownloadPresignedUrl(FolderConstant.IDENTIFICATION_PICTURE, form.getUser().getUuid().toString()))
        );
        String html = processTemplateService.execute(Templates.ADMISSION_TICKET, formMap);

        return generatePdfService.execute(html);
    }
}
