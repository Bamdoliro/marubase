package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SelectFirstPassUseCase {

    private final FormRepository formRepository;
    private final CalculateFormScoreService calculateFormScoreService;

    @Transactional
    public void execute() {
        int regularCount = FixedNumber.REGULAR;
        int meisterTalentCount = FixedNumber.MEISTER_TALENT;
        int socialIntegrationCount = FixedNumber.SOCIAL_INTEGRATION;
        int nationalVeteransEducationCount = FixedNumber.NATIONAL_VETERANS_EDUCATION;
        int specialAdmissionCount = FixedNumber.SPECIAL_ADMISSION;

        List<Form> specialFormList = formRepository.findReceivedSpecialForm();
        List<Form> meisterTalentFormList = classifyFormsByType(specialFormList, FormType::isMeister);
        List<Form> socialIntegrationFormList = classifyFormsByType(specialFormList, FormType::isSocial);

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

        regularCount = calculateMultiple(regularCount);
        meisterTalentCount = calculateMultiple(meisterTalentCount);
        socialIntegrationCount = calculateMultiple(socialIntegrationCount);
        nationalVeteransEducationCount = calculateMultiple(nationalVeteransEducationCount);
        specialAdmissionCount = calculateMultiple(specialAdmissionCount);
        int equalOpportunityCount = (int) Math.round(socialIntegrationCount * 0.5);
        int societyDiversityCount = equalOpportunityCount;


        for (Form form : socialIntegrationFormList) {
            if (form.getType().isEqualOpportunity() && equalOpportunityCount > 0) {
                form.firstPass();
                equalOpportunityCount--;
            } else if (form.getType().isSocietyDiversity() && societyDiversityCount > 0) {
                form.firstPass();
                societyDiversityCount--;
            } else {
                changeToRegularAndCalculateGradeAgain(form);
            }
        }

        for (Form form : meisterTalentFormList) {
            if (meisterTalentCount > 0) {
                form.firstPass();
                meisterTalentCount--;
            } else {
                changeToRegularAndCalculateGradeAgain(form);
            }
        }

        formRepository.flush();
        List<Form> regularOrSupernumeraryFormList = formRepository.findReceivedRegularOrSupernumeraryForm();

        for (Form form : regularOrSupernumeraryFormList) {
            if (form.getType().isRegular() && regularCount > 0) {
                form.firstPass();
                regularCount--;
            } else if (
                    form.getType().isNationalVeteransEducation() &&
                            regularCount > 0 &&
                            nationalVeteransEducationCount > 0
            ) {
                form.firstPass();
                nationalVeteransEducationCount--;
            } else if (
                    form.getType().isSpecialAdmission() &&
                            regularCount > 0 &&
                            specialAdmissionCount > 0
            ) {
                form.firstPass();
                specialAdmissionCount--;
            } else {
                form.firstFail();
            }
        }
    }

    private void changeToRegularAndCalculateGradeAgain(Form form) {
        form.changeToRegular();
        form.getScore().updateSubjectScore(
                calculateFormScoreService.calculateSubjectGradeScore(form)
        );
    }

    private List<Form> classifyFormsByType(List<Form> formList, FormTypeFilter filter) {
        return formList.stream()
                .filter(form -> filter.execute(form.getType()))
                .toList();
    }

    private int calculateMultiple(int count) {
        return (int) Math.round(count * FixedNumber.MULTIPLE);
    }
}

@FunctionalInterface
interface FormTypeFilter {
    boolean execute(FormType type);
}