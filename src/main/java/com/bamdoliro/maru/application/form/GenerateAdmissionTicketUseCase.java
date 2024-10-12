package com.bamdoliro.maru.application.form;

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
import com.bamdoliro.maru.shared.constants.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static com.bamdoliro.maru.shared.constants.Schedule.*;

@RequiredArgsConstructor
@UseCase
public class GenerateAdmissionTicketUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final FileService fileService;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        Map<String, Object> formMap = Map.ofEntries(
                Map.entry("form", form),
                Map.entry("year", Schedule.getAdmissionYear()),
                Map.entry("codingTest", Schedule.toLocaleString(CODING_TEST)),
                Map.entry("ncs", Schedule.toLocaleString(NCS)),
                Map.entry("depthInterview", Schedule.toLocaleString(DEPTH_INTERVIEW)),
                Map.entry("physicalExamination", Schedule.toLocaleString(PHYSICAL_EXAMINATION)),
                Map.entry("announcementOfSecondPass", Schedule.toLocaleString(ANNOUNCEMENT_OF_SECOND_PASS)),
                Map.entry("meisterTalentEntranceTime", Schedule.toLocaleString(MEISTER_TALENT_ENTRANCE_TIME)),
                Map.entry("meisterTalentExclusionEntranceTime", Schedule.toLocaleString(MEISTER_TALENT_EXCLUSION_ENTRANCE_TIME)),
                Map.entry("entranceRegistrationTime", Schedule.toLocaleString(ENTRANCE_REGISTRATION_PERIOD_START, ENTRANCE_REGISTRATION_PERIOD_END)),
                Map.entry("identificationPictureUri", fileService.getPresignedUrl(FolderConstant.IDENTIFICATION_PICTURE, user.getUuid().toString()).getDownloadUrl())
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
