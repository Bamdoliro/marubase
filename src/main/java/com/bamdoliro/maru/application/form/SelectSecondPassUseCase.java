package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.MissingTotalScoreException;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.application.policy.AdmissionPolicyService;
import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

@RequiredArgsConstructor
@UseCase
public class SelectSecondPassUseCase {

    private final FormRepository formRepository;
    private final AdmissionPolicyService admissionPolicyService;
    private final CalculateFormScoreService calculateFormScoreService;

    @Transactional
    public void execute() {
        validate();

        SelectionPolicy policy = admissionPolicyService.selectionForExecution();
        AtomicInteger otherRegionCount = new AtomicInteger(policy.otherRegionCount(false));

        // 1차 합격자 수를 원 정원과 비교해 미달분을 이월한다. 2차는 배수 없이 원 정원으로 선발한다.
        List<Form> specialFormList = formRepository.findFirstPassedSpecialForm();
        List<Form> meisterTalentFormList = classifyFormsByType(specialFormList, FormType::isMeister);
        List<Form> equalOpportunityFormList = classifyFormsByType(specialFormList, FormType::isEqualOpportunity);
        List<Form> societyDiversityFormList = classifyFormsByType(specialFormList, FormType::isSocietyDiversity);

        SelectionQuota quota = SelectionQuota.of(
                policy,
                meisterTalentFormList.size(),
                equalOpportunityFormList.size(),
                societyDiversityFormList.size(),
                IntUnaryOperator.identity()
        );

        processForms(equalOpportunityFormList, quota.equalOpportunity(), this::changeToRegularAndCalculateScoreAgain, otherRegionCount);
        processForms(societyDiversityFormList, quota.societyDiversity(), this::changeToRegularAndCalculateScoreAgain, otherRegionCount);
        processForms(meisterTalentFormList, quota.meisterTalent(), this::changeToRegularAndCalculateScoreAgain,otherRegionCount);

        formRepository.flush();
        List<Form> regularFormList = formRepository.findFirstPassedRegularForm();

        processForms(regularFormList, quota.regular(), Form::fail, otherRegionCount);

        formRepository.flush();
        List<Form> supernumeraryFormList = formRepository.findFirstPassedSupernumeraryForm();
        List<Form> nationalVeteransEducationFormList = classifyFormsByType(supernumeraryFormList, FormType::isNationalVeteransEducation);
        List<Form> specialAdmissionFormList = classifyFormsByType(supernumeraryFormList, FormType::isSpecialAdmission);

        processForms(nationalVeteransEducationFormList, quota.nationalVeteransEducation(), Form::fail, otherRegionCount);
        processForms(specialAdmissionFormList, quota.specialAdmission(), Form::fail, otherRegionCount);
    }

    private void processForms(List<Form> formList, int count, Consumer<Form> action, AtomicInteger otherRegionCount) {
        for (Form form : formList) {
            if (count > 0) {
                if (form.getEducation().getSchool().isBusan()) {
                    form.pass();
                    count--;
                } else if (otherRegionCount.intValue() > 0) {
                    form.pass();
                    otherRegionCount.getAndDecrement();
                    count--;
                } else {
                    action.accept(form);
                }
            } else {
                action.accept(form);
            }
        }
    }

    private void changeToRegularAndCalculateScoreAgain(Form form) {
        form.changeToRegularSecondRound(calculateFormScoreService);
    }

    private void validate() {
        List<Form> firstPassedFormList = formRepository.findByStatus(FormStatus.FIRST_PASSED);
        firstPassedFormList.stream()
                .filter(form -> form.getScore().getTotalScore() == null)
                .findFirst()
                .ifPresent(form -> {
                    throw new MissingTotalScoreException();
                });
    }

    private List<Form> classifyFormsByType(List<Form> formList, FormTypeFilter filter) {
        return formList.stream()
                .filter(form -> filter.execute(form.getType()))
                .toList();
    }
}
