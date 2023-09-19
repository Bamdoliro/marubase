package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.user.domain.User;
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
        List<PassOrFailFormRequest> requestList = request.getFormList().stream()
                .sorted(Comparator.comparingLong(PassOrFailFormRequest::getFormId))
                .toList();

        List<Form> formList = formRepository.findByFormIdList(
                requestList.stream()
                        .map(PassOrFailFormRequest::getFormId)
                        .toList()
        );

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
}
