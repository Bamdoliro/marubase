package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.application.policy.AdmissionPolicyService;
import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@RequiredArgsConstructor
@UseCase
public class SelectFirstPassUseCase {

    private final FormRepository formRepository;
    private final AdmissionPolicyService admissionPolicyService;
    private final CalculateFormScoreService calculateFormScoreService;
    @Transactional
    public void execute() {
        SelectionPolicy policy = admissionPolicyService.selectionForExecution();
        AtomicInteger otherRegionCount = new AtomicInteger(policy.otherRegionCount(true));

        List<Form> specialFormList = formRepository.findApprovedSpecialForm();
        List<Form> meisterTalentFormList = classifyFormsByType(specialFormList, FormType::isMeister);
        List<Form> equalOpportunityFormList = classifyFormsByType(specialFormList, FormType::isEqualOpportunity);
        List<Form> societyDiversityFormList = classifyFormsByType(specialFormList, FormType::isSocietyDiversity);

        SelectionQuota quota = SelectionQuota.of(
                policy,
                meisterTalentFormList.size(),
                equalOpportunityFormList.size(),
                societyDiversityFormList.size(),
                policy::firstRoundCount
        );

        processForms(
                equalOpportunityFormList,
                quota.equalOpportunity(),
                otherRegionCount,
                this::changeToRegularAndCalculateGradeAgain
        );
        processForms(
                societyDiversityFormList,
                quota.societyDiversity(),
                otherRegionCount,
                this::changeToRegularAndCalculateGradeAgain
        );
        processForms(
                meisterTalentFormList,
                quota.meisterTalent(),
                otherRegionCount,
                this::changeToRegularAndCalculateGradeAgain
        );

        formRepository.flush();
        List<Form> regularFormList = formRepository.findApprovedRegularForm();

        processForms(regularFormList, quota.regular(), otherRegionCount, Form::firstFail);

        formRepository.flush();
        List<Form> supernumeraryFormList = formRepository.findApprovedSupernumeraryForm();
        List<Form> nationalVeteransFormList = classifyFormsByType(
                supernumeraryFormList,
                FormType::isNationalVeteransEducation
        );
        List<Form> specialAdmissionFormList = classifyFormsByType(
                supernumeraryFormList,
                FormType::isSpecialAdmission
        );

        processForms(
                nationalVeteransFormList,
                quota.nationalVeteransEducation(),
                otherRegionCount,
                Form::firstFail
        );
        processForms(specialAdmissionFormList, quota.specialAdmission(), otherRegionCount, Form::firstFail);
    }

    private void processForms(
            List<Form> formList,
            int count,
            AtomicInteger otherRegionCount,
            Consumer<Form> action
    ) {
        if (formList.isEmpty())
            return;

        if (count <= 0) {
            formList.forEach(action);
            return;
        }

        Double lastScore = formList.get(Math.min(count, formList.size()) - 1).getScore().getFirstRoundScore();

        for (Form form : formList) {
            if (count > 0) {
                if (form.getEducation().getSchool().isBusan()) {
                    form.firstPass();
                    count--;
                } else if (otherRegionCount.intValue() > 0) {
                    form.firstPass();
                    otherRegionCount.getAndDecrement();
                    count--;
                } else {
                    action.accept(form);
                }
            } else if (form.getScore().getFirstRoundScore().equals(lastScore)) {
                form.firstPass();
            } else {
                action.accept(form);
            }
        }
    }

    private void changeToRegularAndCalculateGradeAgain(Form form) {
        form.changeToRegularFirstRound(calculateFormScoreService);
    }

    private List<Form> classifyFormsByType(List<Form> formList, FormTypeFilter filter) {
        return formList.stream()
                .filter(form -> filter.execute(form.getType()))
                .toList();
    }
}

@FunctionalInterface
interface FormTypeFilter {
    boolean execute(FormType type);
}
