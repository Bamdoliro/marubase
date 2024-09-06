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

import static com.bamdoliro.maru.shared.constants.Schedule.ANNOUNCEMENT_OF_FIRST_PASS;

@RequiredArgsConstructor
@UseCase
public class GenerateProofOfApplicationUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final FileService fileService;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        Map<String, Object> formMap = Map.of(
                "form", form,
                "year", Schedule.getAdmissionYear(),
                "announcement_of_first_pass", Schedule.toLocaleString(ANNOUNCEMENT_OF_FIRST_PASS),
                "identificationPictureUri", fileService.getPresignedUrl(FolderConstant.IDENTIFICATION_PICTURE, user.getUuid().toString()).getDownloadUrl()
        );
        String html = processTemplateService.execute(Templates.PROOF_OF_APPLICATION, formMap);
        ByteArrayOutputStream outputStream = generatePdfService.execute(html);

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private void validateFormStatus(Form form) {
        if (form.isSubmitted()) {
            throw new InvalidFormStatusException();
        }
    }
}
