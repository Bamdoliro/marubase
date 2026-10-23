package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormResultRequestException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.application.schedule.AdmissionScheduleFacade;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormRequest;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@UseCase
public class PassOrFailFormUseCase {

    private final FormRepository formRepository;
    private final AdmissionScheduleFacade admissionScheduleFacade;

    @Transactional
    public void execute(PassOrFailFormListRequest request) {
        validateRequest(request);
        admissionScheduleFacade.getCurrentScheduleForUpdate();
        List<PassOrFailFormRequest> requestList = getSortedList(request);

        List<Form> formList = formRepository.findAllByIdsForUpdate(
                requestList.stream()
                        .map(PassOrFailFormRequest::getFormId)
                        .toList()
        );

        validate(requestList, formList);

        Map<Long, Boolean> results = requestList.stream().collect(Collectors.toMap(
                PassOrFailFormRequest::getFormId, PassOrFailFormRequest::getPass));
        for (Form form : formList) {
            boolean pass = results.get(form.getId());

            if (pass) {
                form.pass();
            } else {
                form.fail();
            }
        }
    }

    private void validateRequest(PassOrFailFormListRequest request) {
        if (request == null || request.getFormList() == null || request.getFormList().isEmpty()
                || request.getFormList().stream().anyMatch(item -> item == null
                || item.getFormId() == null || item.getPass() == null)
                || request.getFormList().stream().map(PassOrFailFormRequest::getFormId)
                .distinct().count() != request.getFormList().size()) {
            throw new InvalidFormResultRequestException();
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
        Set<FormStatus> allowed = Set.of(FormStatus.FIRST_PASSED, FormStatus.PASSED, FormStatus.FAILED);
        if (formList.stream().anyMatch(form -> !allowed.contains(form.getStatus()))) {
            throw new InvalidFormStatusException();
        }
    }
}
