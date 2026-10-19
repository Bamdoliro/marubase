package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.validator.DefaultFileValidator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.annotation.ValidateApplicationFormPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.util.Set;

@RequiredArgsConstructor
@UseCase
public class UploadFormUseCase {

    private final FileService fileService;
    private final FormFacade formFacade;

    @ValidateApplicationFormPeriod
    public UrlResponse execute(User user, FileMetadata fileMetadata) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        return fileService.getPresignedUrl(FolderConstant.FORM, user.getUuid().toString(), fileMetadata, metadata ->
                DefaultFileValidator.validate(metadata, Set.of(MediaType.APPLICATION_PDF), 30)
        );
    }

    private void validateFormStatus(Form form) {
        if (!(form.isSubmitted() || form.isRejected())) {
            throw new InvalidFormStatusException();
        }
    }
}
