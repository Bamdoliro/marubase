package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormResultResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/forms")
@RestController
public class UserFormController {

    private final SubmitFormUseCase submitFormUseCase;
    private final SubmitFinalFormUseCase submitFinalFormUseCase;
    private final EnterFormUseCase enterFormUseCase;
    private final QueryFormStatusUseCase queryFormStatusUseCase;
    private final UpdateFormUseCase updateFormUseCase;
    private final UploadIdentificationPictureUseCase uploadIdentificationPictureUseCase;
    private final UploadFormUseCase uploadFormUseCase;
    private final ExportFormUseCase exportFormUseCase;
    private final DownloadAdmissionAndPledgeFormatUseCase downloadAdmissionAndPledgeFormatUseCase;
    private final UploadAdmissionAndPledgeUseCase uploadAdmissionAndPledgeUseCase;
    private final QueryFirstFormResultUseCase queryFirstFormResultUseCase;
    private final QueryFinalFormResultUseCase queryFinalFormResultUseCase;
    private final GenerateAdmissionTicketUseCase generateAdmissionTicketUseCase;
    private final GenerateProofOfApplicationUseCase generateProofOfApplicationUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void submitForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody @Valid SubmitFormRequest request
    ) {
        submitFormUseCase.execute(user, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping
    public void submitFinalForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        submitFinalFormUseCase.execute(user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/enter")
    public void enterForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        enterFormUseCase.execute(user);
    }

    @GetMapping("/status")
    public SingleCommonResponse<FormSimpleResponse> getFormStatus(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return SingleCommonResponse.ok(
                queryFormStatusUseCase.execute(user)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void updateForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody @Valid UpdateFormRequest request
    ) {
        updateFormUseCase.execute(user, request);
    }

    @PostMapping("/identification-picture")
    public SingleCommonResponse<UrlResponse> uploadIdentificationPicture(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody @Valid FileMetadata metadata
    ) {
        return SingleCommonResponse.ok(
                uploadIdentificationPictureUseCase.execute(user, metadata)
        );
    }

    @PostMapping("/form-document")
    public SingleCommonResponse<UrlResponse> uploadFormDocument(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody @Valid FileMetadata metadata
    ) {
        return SingleCommonResponse.ok(
                uploadFormUseCase.execute(user, metadata)
        );
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(exportFormUseCase.execute(user));
    }

    @GetMapping("/admission-and-pledge")
    public ResponseEntity<Resource> downloadAdmissionAndPledgeFormat(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(downloadAdmissionAndPledgeFormatUseCase.execute(user));
    }

    @PostMapping("/admission-and-pledge")
    public SingleCommonResponse<UrlResponse> uploadAdmissionAndPledge(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @RequestBody FileMetadata metadata
    ) {
        return SingleCommonResponse.ok(
                uploadAdmissionAndPledgeUseCase.execute(user, metadata)
        );
    }

    @GetMapping("/result/first")
    public SingleCommonResponse<FormResultResponse> getFirstFormResult(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return SingleCommonResponse.ok(
                queryFirstFormResultUseCase.execute(user)
        );
    }

    @GetMapping("/result/final")
    public SingleCommonResponse<FormResultResponse> getFinalFormResult(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return SingleCommonResponse.ok(
                queryFinalFormResultUseCase.execute(user)
        );
    }

    @GetMapping("/admission-ticket")
    public ResponseEntity<Resource> generateAdmissionTicket(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(generateAdmissionTicketUseCase.execute(user));
    }

    @GetMapping("/proof-of-application")
    public ResponseEntity<Resource> generateProofOfApplication(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(generateProofOfApplicationUseCase.execute(user));
    }
}