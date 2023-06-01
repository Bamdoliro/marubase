package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.ApproveFormUseCase;
import com.bamdoliro.maru.application.form.QueryFormUseCase;
import com.bamdoliro.maru.application.form.QuerySubmittedFormUseCase;
import com.bamdoliro.maru.application.form.RejectFormUseCase;
import com.bamdoliro.maru.application.form.SubmitFormUseCase;
import com.bamdoliro.maru.application.form.UpdateFormUseCase;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormResponse;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void submitForm(@RequestBody @Valid FormRequest request) {
        submitFormUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{form-id}/approve")
    public void approveForm(@PathVariable(name = "form-id") Long formId) {
        approveFormUseCase.execute(formId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{form-id}/reject")
    public void rejectForm(@PathVariable(name = "form-id") Long formId) {
        rejectFormUseCase.execute(formId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/review")
    public ListCommonResponse<FormSimpleResponse> getSubmittedFormList() {
        return ListCommonResponse.ok(
                querySubmittedFormUseCase.execute()
        );
    }

    @GetMapping("/{form-id}")
    public SingleCommonResponse<FormResponse> getForm(@PathVariable(name = "form-id") Long formId) {
        return SingleCommonResponse.ok(
                queryFormUseCase.execute(formId)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{form-id}")
    public void updateForm(@PathVariable(name = "form-id") Long formId, @RequestBody @Valid FormRequest request) {
        updateFormUseCase.execute(formId, request);
    }
}