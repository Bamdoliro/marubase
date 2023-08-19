package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.InvalidFromStatusException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.shared.annotation.UseCase;
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

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        Map<String, Object> formMap = Map.of(
                "form", form
        );
        String template = processTemplateService.execute("admission-ticket", formMap);
        ByteArrayOutputStream outputStream = generatePdfService.execute(template);

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private void validateFormStatus(Form form) {
        if (!form.isFirstPassed()) {
            throw new InvalidFromStatusException();
        }
    }
}
