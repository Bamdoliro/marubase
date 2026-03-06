package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.*;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.response.AdmissionAndPledgeUrlResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormUrlResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/forms")
@RestController
public class AdminFormController {

    private final ApproveFormUseCase approveFormUseCase;
    private final RejectFormUseCase rejectFormUseCase;
    private final ReceiveFormUseCase receiveFormUseCase;
    private final QuerySubmittedFormUseCase querySubmittedFormUseCase;
    private final QueryAllFormUseCase queryAllFormUseCase;
    private final GenerateAllAdmissionTicketUseCase generateAllAdmissionTicketUseCase;
    private final DownloadSecondRoundScoreFormatUseCase downloadSecondRoundScoreFormatUseCase;
    private final UpdateSecondRoundScoreUseCase updateSecondRoundScoreUseCase;
    private final ExportFinalPassedFormUseCase exportFinalPassedFormUseCase;
    private final ExportFirstScoreUseCase exportFirstScoreUseCase;
    private final ExportFirstRoundResultUseCase exportFirstRoundResultUseCase;
    private final ExportSecondRoundResultUseCase exportSecondRoundResultUseCase;
    private final ExportResultUseCase exportResultUseCase;
    private final ExportSubjectGradeDetailUseCase exportSubjectGradeDetailUseCase;
    private final PassOrFailFormUseCase passOrFailFormUseCase;
    private final QueryFormUrlUseCase queryFormUrlUseCase;
    private final SelectSecondPassUseCase selectSecondPassUseCase;
    private final QueryAdmissionAndPledgeUseCase queryAdmissionAndPledgeUseCase;

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

    @GetMapping("/review")
    public ListCommonResponse<FormSimpleResponse> getSubmittedFormList(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) {
        return ListCommonResponse.ok(
                querySubmittedFormUseCase.execute()
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

    @GetMapping("/admission-tickets")
    public ResponseEntity<Resource> generateAllAdmissionTicket(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(generateAllAdmissionTicketUseCase.execute());
    }

    @GetMapping("/second-round/format")
    public ResponseEntity<Resource> downloadSecondRoundScoreFormat(
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
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest()
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

    @GetMapping("/xlsx/first-score")
    public ResponseEntity<Resource> exportFirstScoreResult(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportFirstScoreUseCase.execute());
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

    @GetMapping("/xlsx/subject-grade-detail")
    public ResponseEntity<Resource> exportSubjectGradeDetail(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user
    ) throws IOException {
        String filename = "합격자성적상세.xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header("Content-Disposition",
                        "attachment; filename=\"export.xlsx\"; filename*=UTF-8''" + encodedFilename)
                .body(exportSubjectGradeDetailUseCase.execute());
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