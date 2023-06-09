package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.ApproveFormUseCase;
import com.bamdoliro.maru.application.form.QueryFormUseCase;
import com.bamdoliro.maru.application.form.QuerySubmittedFormUseCase;
import com.bamdoliro.maru.application.form.RejectFormUseCase;
import com.bamdoliro.maru.application.form.SubmitFormUseCase;
import com.bamdoliro.maru.application.form.UpdateFormUseCase;
import com.bamdoliro.maru.application.form.UploadFormUseCase;
import com.bamdoliro.maru.application.form.UploadIdentificationPictureUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/form")
@RestController
public class FormController {

    private final SubmitFormUseCase submitFormUseCase;
    private final ApproveFormUseCase approveFormUseCase;
    private final RejectFormUseCase rejectFormUseCase;
    private final QuerySubmittedFormUseCase querySubmittedFormUseCase;
    private final QueryFormUseCase queryFormUseCase;
    private final UpdateFormUseCase updateFormUseCase;
    private final UploadIdentificationPictureUseCase uploadIdentificationPictureUseCase;
    private final UploadFormUseCase uploadFormUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void submitForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody @Valid FormRequest request
    ) {
        submitFormUseCase.execute(user, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{form-id}/approve")
    public void approveForm(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "form-id") Long formId
    ) {
        approveFormUseCase.execute(formId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{form-id}/reject")
    public void rejectForm(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "form-id") Long formId
    ) {
        rejectFormUseCase.execute(formId);
    }

    @GetMapping("/review")
    public ListCommonResponse<FormSimpleResponse> getSubmittedFormList(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) {
        return ListCommonResponse.ok(
                querySubmittedFormUseCase.execute()
        );
    }

    @GetMapping("/{form-id}")
    public SingleCommonResponse<FormResponse> getForm(
            @AuthenticationPrincipal(authority = Authority.ALL) User user,
            @PathVariable(name = "form-id") Long formId
    ) {
        return SingleCommonResponse.ok(
                queryFormUseCase.execute(user, formId)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{form-id}")
    public void updateForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @PathVariable(name = "form-id") Long formId,
            @RequestBody @Valid FormRequest request
    ) {
        updateFormUseCase.execute(user, formId, request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/identification-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public SingleCommonResponse<UploadResponse> uploadIdentificationPicture(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestPart(value = "image") MultipartFile image
    ) {
        return SingleCommonResponse.ok(
                uploadIdentificationPictureUseCase.execute(image)
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/form-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SingleCommonResponse<UploadResponse> uploadFormDocument(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return SingleCommonResponse.ok(
                uploadFormUseCase.execute(file)
        );
    }
}