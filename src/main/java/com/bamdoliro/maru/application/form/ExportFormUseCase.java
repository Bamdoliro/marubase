package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@UseCase
public class ExportFormUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        Map<String, Object> formMap = Map.of("form", form);

        List<ByteArrayOutputStream> htmlStreams = getRequiredTemplates(form.getType())
                .stream()
                .map((t) -> processTemplateService.execute(t, formMap))
                .map(generatePdfService::execute)
                .toList();

//        return new ByteArrayResource(null);
        return null;
    }

    private List<String> getRequiredTemplates(FormType formType) {
        if (formType.isRegular()) {
            return List.of(
                    Templates.FORM,
                    Templates.DOCUMENT,
                    Templates.NO_SMOKING
            );
        }

        return List.of(
                Templates.FORM,
                Templates.DOCUMENT,
                Templates.RECOMMENDATION,
                Templates.NO_SMOKING
        );
    }
}
