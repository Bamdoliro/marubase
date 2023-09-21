package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormUrlResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QueryFormUrlUseCase {

    private final FormRepository formRepository;

    public List<FormUrlResponse> execute(List<Long> formIdList) {
        return formRepository.findFormUrlByFormIdList(formIdList).stream()
                .map(FormUrlResponse::new)
                .toList();
    }
}
