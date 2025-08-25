package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.exception.OutOfAdmissionAndPledgePeriodException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.validator.DefaultFileValidator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@UseCase
public class UploadAdmissionAndPledgeUseCase {

    private final FileService fileService;
    private final FormFacade formFacade;

    public UrlResponse execute(User user, FileMetadata fileMetadata) {
        validateApplicationPeriod(LocalDateTime.now());
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        return fileService.getPresignedUrl(FolderConstant.ADMISSION_AND_PLEDGE, user.getUuid().toString(), fileMetadata, metadata ->
                DefaultFileValidator.validate(metadata, Set.of(MediaType.APPLICATION_PDF), 20)
        );
    }

    private void validateApplicationPeriod(LocalDateTime now) {
        if (now.isBefore(Schedule.ADMISSION_AND_PLEDGE_START) || now.isAfter(Schedule.ADMISSION_AND_PLEDGE_END)) {
            throw new OutOfAdmissionAndPledgePeriodException();
        }
    }

    private void validateFormStatus(Form form) {
        if(!form.isPassedNow() && !form.isEntered())
            throw new InvalidFormStatusException();
    }
}
