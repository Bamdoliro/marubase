package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
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
import com.bamdoliro.maru.shared.util.ScheduleFormatter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
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
    private final AdmissionScheduleFacade admissionScheduleFacade;

    public ByteArrayResource execute() {
        List<Form> formList = formRepository.findByStatus(FormStatus.FIRST_PASSED)
                .stream()
                .sorted(Comparator.comparing(Form::getExaminationNumber, Comparator.nullsLast(Long::compareTo)))
                .toList();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        formList.forEach(form -> mergePdfService.execute(pdfMerger, generateAdmissionTicket(form)));

        mergedDocument.close();
        pdfMerger.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private ByteArrayOutputStream generateAdmissionTicket(Form form) {
        AdmissionSchedule schedule = admissionScheduleFacade.getCurrentSchedule();

        Map<String, Object> formMap = Map.ofEntries(
                Map.entry("form", form),
                Map.entry("year", schedule.getAdmissionYear()),
                Map.entry("codingTest", ScheduleFormatter.toLocaleString(schedule.getCodingTest())),
                Map.entry("ncs", ScheduleFormatter.toLocaleString(schedule.getNcs())),
                Map.entry("depthInterview", ScheduleFormatter.toLocaleString(schedule.getDepthInterview())),
                Map.entry(
                        "physicalExamination",
                        ScheduleFormatter.toLocaleString(schedule.getPhysicalExamination())
                ),
                Map.entry(
                        "announcementOfSecondPass",
                        ScheduleFormatter.toLocaleString(schedule.getAnnouncementOfSecondPass())
                ),
                Map.entry(
                        "meisterTalentEntranceTime",
                        ScheduleFormatter.toLocaleString(schedule.getMeisterTalentEntranceTime())
                ),
                Map.entry(
                        "meisterTalentExclusionEntranceTime",
                        ScheduleFormatter.toLocaleString(
                                schedule.getMeisterTalentExclusionEntranceTime()
                        )
                ),
                Map.entry(
                        "entranceRegistrationTime",
                        ScheduleFormatter.toLocaleString(
                                schedule.getEntranceRegistrationPeriodStart(),
                                schedule.getEntranceRegistrationPeriodEnd()
                        )
                ),
                Map.entry(
                        "identificationPictureUri",
                        fileService.getDownloadPresignedUrl(
                                FolderConstant.IDENTIFICATION_PICTURE,
                                form.getUser().getUuid().toString()
                        )
                )
        );
        String html = processTemplateService.execute(Templates.ADMISSION_TICKET, formMap);

        return generatePdfService.execute(html);
    }
}
