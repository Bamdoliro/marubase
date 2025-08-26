package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.*;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import com.bamdoliro.maru.presentation.form.dto.request.UpdateFormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.*;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/forms")
@RestController
public class FormController {

    private final SubmitFormUseCase submitFormUseCase;
    private final SubmitFinalFormUseCase submitFinalFormUseCase;
    private final ApproveFormUseCase approveFormUseCase;
    private final RejectFormUseCase rejectFormUseCase;
    private final ReceiveFormUseCase receiveFormUseCase;
    private final EnterFormUseCase enterFormUseCase;
    private final QuerySubmittedFormUseCase querySubmittedFormUseCase;
    private final QueryFormUseCase queryFormUseCase;
    private final QueryFormStatusUseCase queryFormStatusUseCase;
    private final UpdateFormUseCase updateFormUseCase;
    private final UploadIdentificationPictureUseCase uploadIdentificationPictureUseCase;
    private final UploadFormUseCase uploadFormUseCase;
    private final ExportFormUseCase exportFormUseCase;
    private final DownloadAdmissionAndPledgeFormatUseCase downloadAdmissionAndPledgeFormatUseCase;
    private final UploadAdmissionAndPledgeUseCase uploadAdmissionAndPledgeUseCase;
    private final QueryAllFormUseCase queryAllFormUseCase;
    private final QueryFirstFormResultUseCase queryFirstFormResultUseCase;
    private final QueryFinalFormResultUseCase queryFinalFormResultUseCase;
    private final GenerateAdmissionTicketUseCase generateAdmissionTicketUseCase;
    private final GenerateProofOfApplicationUseCase generateProofOfApplicationUseCase;
    private final DownloadSecondRoundScoreFormatUseCase downloadSecondRoundScoreFormatUseCase;
    private final UpdateSecondRoundScoreUseCase updateSecondRoundScoreUseCase;
    private final ExportFinalPassedFormUseCase exportFinalPassedFormUseCase;
    private final ExportFirstRoundResultUseCase exportFirstRoundResultUseCase;
    private final ExportSecondRoundResultUseCase exportSecondRoundResultUseCase;
    private final ExportResultUseCase exportResultUseCase;
    private final PassOrFailFormUseCase passOrFailFormUseCase;
    private final QueryFormUrlUseCase queryFormUrlUseCase;
    private final SelectSecondPassUseCase selectSecondPassUseCase;
    private final GenerateAllAdmissionTicketUseCase generateAllAdmissionTicketUseCase;
    private final QueryAdmissionAndPledgeUseCase queryAdmissionAndPledgeUseCase;

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
    public void submitForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        submitFinalFormUseCase.execute(user);
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{form-id}/receive")
    public void receiveForm(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "form-id") Long formId
    ) {
        receiveFormUseCase.execute(formId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/enter")
    public void enterForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        enterFormUseCase.execute(user);
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

    @GetMapping("/status")
    public SingleCommonResponse<FormSimpleResponse> getForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return SingleCommonResponse.ok(
                queryFormStatusUseCase.execute(user)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{form-id}")
    public void updateForm(
            @AuthenticationPrincipal(authority = Authority.USER) User user,
            @PathVariable(name = "form-id") Long formId,
            @RequestBody @Valid UpdateFormRequest request
    ) {
        updateFormUseCase.execute(user, formId, request);
    }

    @PostMapping( "/identification-picture")
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

    @GetMapping( "/admission-and-pledge")
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

    @GetMapping
    public ListCommonResponse<FormSimpleResponse> getFormList(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestParam(name = "status", required = false) FormStatus status,
            @RequestParam(name = "type", required = false) FormType type,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        return ListCommonResponse.ok(
                queryAllFormUseCase.execute(status, type, sort)
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

    @GetMapping("/admission-tickets")
    public ResponseEntity<Resource> generateAllAdmissionTicket(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(generateAllAdmissionTicketUseCase.execute());
    }

    @GetMapping("/proof-of-application")
    public ResponseEntity<Resource> generateProofOfApplication(
            @AuthenticationPrincipal(authority = Authority.USER) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(generateProofOfApplicationUseCase.execute(user));
    }

    @GetMapping("/second-round/format")
    public ResponseEntity<Resource> downloadSecondRoundScoreFormatUseCase(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(downloadSecondRoundScoreFormatUseCase.execute());
    }

    @PatchMapping("/second-round/score")
    public ResponseEntity<Resource> updateSecondRoundScore(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestPart(value = "xlsx") MultipartFile file
    ) throws IOException {
        Resource response = updateSecondRoundScoreUseCase.execute(file);
        if (response == null) {
            return ResponseEntity
                    .noContent()
                    .build();
        } else {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(response);
        }
    }

    @GetMapping("/xlsx/final-passed")
    public ResponseEntity<Resource> exportFinalPassedForm(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportFinalPassedFormUseCase.execute());
    }

    @GetMapping("/xlsx/first-round")
    public ResponseEntity<Resource> exportFirstRoundResult(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportFirstRoundResultUseCase.execute());
    }

    @GetMapping("/xlsx/second-round")
    public ResponseEntity<Resource> exportSecondRoundResult(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportSecondRoundResultUseCase.execute());
    }

    @GetMapping("/xlsx/result")
    public ResponseEntity<Resource> exportResult(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportResultUseCase.execute());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/second-round/result")
    public void passOrFailForm(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid PassOrFailFormListRequest request
    ) {
        passOrFailFormUseCase.execute(request);
    }

    @GetMapping("/form-url")
    public ListCommonResponse<FormUrlResponse> getFormUrl(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestParam(name = "id-list") List<Long> formIdList
    ) {
        return CommonResponse.ok(
                queryFormUrlUseCase.execute(formIdList)
        );
    }


    @GetMapping("/admission-and-pledges")
    public ListCommonResponse<AdmissionAndPledgeUrlResponse> getAdmissionAndPledges(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestParam(name = "id-list") List<Long> formIdList
    ) {
        return CommonResponse.ok(
                queryAdmissionAndPledgeUseCase.execute(formIdList)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/second-round/select")
    public void selectSecondPass(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) {
        selectSecondPassUseCase.execute();
    }
}