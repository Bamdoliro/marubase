package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.presentation.form.dto.request.FormListRequest;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@UseCase
public class QueryAllFormUseCase {

    private final FormRepository formRepository;

    public List<FormSimpleResponse> execute(FormListRequest request) {
        List<Form> formList = new ArrayList<>(findFormsByDisplayStatus(request.getStatus()).stream()
                .filter(form -> Objects.isNull(request.getType()) || form.getType().equals(request.getType()))
                .filter(form -> request.getMentoringProgram() == null
                        || form.getGrade().isMentoringProgram() == request.getMentoringProgram())
                .filter(form -> matchesSchool(form, request.getSchoolName()))
                .filter(form -> matchesScore(form.getScore().getFirstRoundScore(),
                        request.getFirstRoundScoreMin(), request.getFirstRoundScoreMax()))
                .filter(form -> matchesScore(form.getScore().getTotalScore(),
                        request.getTotalScoreMin(), request.getTotalScoreMax()))
                .toList());

        String sort = request.getSort();
        if (sort != null) {
            switch (sort) {
                case "first-round-score-asc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getFirstRoundScore(), Comparator.nullsLast(Comparator.naturalOrder())));
                case "first-round-score-desc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getFirstRoundScore(), Comparator.nullsLast(Comparator.reverseOrder())));
                case "total-score-asc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.nullsLast(Comparator.naturalOrder())));
                case "total-score-desc" ->
                        formList.sort(Comparator.comparing(form -> form.getScore().getTotalScore(), Comparator.nullsLast(Comparator.reverseOrder())));
                case "form-id" -> formList.sort(Comparator.comparing(Form::getId));
                default -> formList.sort(Comparator.comparing(Form::getExaminationNumber));
            }
        } else {
            formList.sort(Comparator.comparing(Form::getExaminationNumber));
        }

        return formList.stream()
                .map(FormSimpleResponse::new)
                .toList();
    }

    // 관리자 목록의 표시 기준이다. 선발·문자 발송 등에서 사용하는 정확한 상태 조회와 분리한다.
    private List<Form> findFormsByDisplayStatus(FormStatus status) {
        if (status == null) {
            return formRepository.findByStatus(null);
        }
        EnumSet<FormStatus> statuses = switch (status) {
            case FINAL_SUBMITTED -> EnumSet.complementOf(EnumSet.of(FormStatus.SUBMITTED));
            case APPROVED -> EnumSet.of(FormStatus.APPROVED, FormStatus.FIRST_PASSED,
                    FormStatus.FIRST_FAILED, FormStatus.PASSED, FormStatus.FAILED,
                    FormStatus.NO_SHOW, FormStatus.ENTERED);
            case FIRST_PASSED -> EnumSet.of(FormStatus.FIRST_PASSED, FormStatus.PASSED,
                    FormStatus.FAILED, FormStatus.NO_SHOW, FormStatus.ENTERED);
            case PASSED -> EnumSet.of(FormStatus.PASSED, FormStatus.ENTERED);
            default -> EnumSet.of(status);
        };
        if (statuses.size() == 1) {
            return formRepository.findByStatus(status);
        }
        return formRepository.findByStatusIn(statuses).stream()
                .sorted(Comparator.comparing(Form::getExaminationNumber))
                .toList();
    }

    private boolean matchesSchool(Form form, String schoolName) {
        if (schoolName == null || schoolName.isBlank()) {
            return true;
        }
        if (form.getEducation().getSchool() == null) {
            return false;
        }
        String name = form.getEducation().getSchool().getName();
        return name != null && name.contains(schoolName.trim());
    }

    private boolean matchesScore(Double score, Double min, Double max) {
        if (min == null && max == null) {
            return true;
        }
        return score != null && (min == null || score >= min) && (max == null || score <= max);
    }
}
