package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.SubmitFormUseCase;
import com.bamdoliro.maru.presentation.form.dto.request.FormRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void submitForm(@RequestBody @Valid FormRequest request) {
        submitFormUseCase.execute(request);
    }
}
