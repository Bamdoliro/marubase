package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class PassOrFailFormUseCase {

    private final FormRepository formRepository;

    @Transactional
    public void execute(PassOrFailFormListRequest request) {
        List<PassOrFailFormRequest> requestList = getSortedList(request);

        List<Form> formList = formRepository.findByFormIdList(
                requestList.stream()
                        .map(PassOrFailFormRequest::getFormId)
                        .toList()
        );

        validate(requestList, formList);

        for (int i = 0; i < formList.size(); i++) {
            Form form = formList.get(i);
            boolean pass = requestList.get(i).isPass();

            if (pass) {
                form.pass();
            } else {
                form.fail();
            }
        }
    }

    private List<PassOrFailFormRequest> getSortedList(PassOrFailFormListRequest request) {
        return request.getFormList().stream()
                .sorted(Comparator.comparingLong(PassOrFailFormRequest::getFormId))
                .toList();
    }

    private void validate(List<PassOrFailFormRequest> requestList, List<Form> formList) {
        if (requestList.size() != formList.size()) {
            throw new FormNotFoundException();
        }
    }
}
