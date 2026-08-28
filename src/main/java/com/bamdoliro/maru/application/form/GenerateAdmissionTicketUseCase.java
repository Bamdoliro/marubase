package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.domain.schedule.domain.AdmissionSchedule;
import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.ScheduleFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@RequiredArgsConstructor
@UseCase
public class GenerateAdmissionTicketUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final FileService fileService;
    private final AdmissionScheduleFacade admissionScheduleFacade;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);
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
                                user.getUuid().toString()
                        )
                )
                );
        String html = processTemplateService.execute(Templates.ADMISSION_TICKET, formMap);
        ByteArrayOutputStream outputStream = generatePdfService.execute(html);

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private void validateFormStatus(Form form) {
        if (!form.isFirstPassedNow()) {
            throw new InvalidFormStatusException();
        }
    }
}
