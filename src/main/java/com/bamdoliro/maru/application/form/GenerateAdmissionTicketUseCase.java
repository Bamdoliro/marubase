package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static com.bamdoliro.maru.shared.constants.Schedule.CODING_TEST;
import static com.bamdoliro.maru.shared.constants.Schedule.DEPTH_INTERVIEW;
import static com.bamdoliro.maru.shared.constants.Schedule.NCS;
import static com.bamdoliro.maru.shared.constants.Schedule.PHYSICAL_EXAMINATION;

@RequiredArgsConstructor
@UseCase
public class GenerateAdmissionTicketUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        Map<String, Object> formMap = Map.of(
                "form", form,
                "year", Schedule.getAdmissionYear(),
                "codingTest", Schedule.toLocaleString(CODING_TEST),
                "ncs", Schedule.toLocaleString(NCS),
                "depthInterview", Schedule.toLocaleString(DEPTH_INTERVIEW),
                "physicalExamination", Schedule.toLocaleString(PHYSICAL_EXAMINATION)
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
