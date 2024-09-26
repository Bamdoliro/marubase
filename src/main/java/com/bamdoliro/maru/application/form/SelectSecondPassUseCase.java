package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.MissingTotalScoreException;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@UseCase
public class SelectSecondPassUseCase {

    private final FormRepository formRepository;
    private final CalculateFormScoreService calculateFormScoreService;

    @Transactional
    public void execute() {
        validate();

        int regularCount = FixedNumber.REGULAR;
        int meisterTalentCount = FixedNumber.MEISTER_TALENT;
        int socialIntegrationCount = FixedNumber.SOCIAL_INTEGRATION;
        int nationalVeteransEducationCount = FixedNumber.NATIONAL_VETERANS_EDUCATION;
        int specialAdmissionCount = FixedNumber.SPECIAL_ADMISSION;

        List<Form> specialFormList = formRepository.findFirstPassedSpecialForm();
        List<Form> meisterTalentFormList = classifyFormsByType(specialFormList, FormType::isMeister);
        List<Form> socialIntegrationFormList = classifyFormsByType(specialFormList, FormType::isSocial);
        List<Form> equalOpportunityFormList = classifyFormsByType(specialFormList, FormType::isEqualOpportunity);
        List<Form> societyDiversityFormList = classifyFormsByType(specialFormList, FormType::isSocietyDiversity);

        if (meisterTalentFormList.size() < FixedNumber.MEISTER_TALENT) {
            int gap = FixedNumber.MEISTER_TALENT - meisterTalentFormList.size();
            regularCount += gap;
            meisterTalentCount -= gap;
        }

        if (socialIntegrationFormList.size() < FixedNumber.SOCIAL_INTEGRATION) {
            int gap = FixedNumber.SOCIAL_INTEGRATION - socialIntegrationFormList.size();
            regularCount += gap;
            socialIntegrationCount -= gap;
        }

        int equalOpportunityCount = (int) Math.round(socialIntegrationCount * 0.5);
        int societyDiversityCount = equalOpportunityCount;

        processForms(equalOpportunityFormList, equalOpportunityCount, this::changeToRegularAndCalculateScoreAgain);
        processForms(societyDiversityFormList, societyDiversityCount, this::changeToRegularAndCalculateScoreAgain);
        processForms(meisterTalentFormList, meisterTalentCount, this::changeToRegularAndCalculateScoreAgain);

        formRepository.flush();
        List<Form> regularFormList = formRepository.findFirstPassedRegularForm();

        processForms(regularFormList, regularCount, Form::fail);

        formRepository.flush();
        List<Form> supernumeraryFormList = formRepository.findFirstPassedSupernumeraryForm();
        List<Form> nationalVeteransEducationFormList = classifyFormsByType(supernumeraryFormList, FormType::isNationalVeteransEducation);
        List<Form> specialAdmissionFormList = classifyFormsByType(supernumeraryFormList, FormType::isSpecialAdmission);

        processForms(nationalVeteransEducationFormList, nationalVeteransEducationCount, Form::fail);
        processForms(specialAdmissionFormList, specialAdmissionCount, Form::fail);
    }

    private void processForms(List<Form> formList, int count, Consumer<Form> action) {
        for (Form form: formList) {
            if (count > 0) {
                form.pass();
                count--;
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
                .forEach(form -> {throw new MissingTotalScoreException();});
    }

    private List<Form> classifyFormsByType(List<Form> formList, FormTypeFilter filter) {
        return formList.stream()
                .filter(form -> filter.execute(form.getType()))
                .toList();
    }
}
