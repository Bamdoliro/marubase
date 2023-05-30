package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.ApproveFormUseCase;
import com.bamdoliro.maru.application.form.RejectFormUseCase;
import com.bamdoliro.maru.application.form.SubmitFormUseCase;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}
