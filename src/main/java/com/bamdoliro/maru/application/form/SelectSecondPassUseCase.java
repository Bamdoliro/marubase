package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.MissingTotalScoreException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.variable.AdmissionCapacity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class SelectSecondPassUseCase {

    private final FormRepository formRepository;

    @Transactional
    public void execute() {
        validate();

        int regularCount = AdmissionCapacity.regular;
        int meisterTalentCount = AdmissionCapacity.meisterTalent;
        int socialIntegrationCount = AdmissionCapacity.socialIntegration;
        int nationalVeteransEducationCount = FixedNumber.NATIONAL_VETERANS_EDUCATION;
        int specialAdmissionCount = FixedNumber.SPECIAL_ADMISSION;
        int equalOpportunityCount = (int) Math.round(socialIntegrationCount * 0.5);
        int societyDiversityCount = equalOpportunityCount;

        List<Form> specialFormList = formRepository.findFirstPassedSpecialForm();
        List<Form> meisterTalentFormList = classifyFormsByType(specialFormList, FormType::isMeister);
        List<Form> socialIntegrationFormList = classifyFormsByType(specialFormList, FormType::isSocial);

        for (Form form : socialIntegrationFormList) {
            if (form.getType().isEqualOpportunity() && equalOpportunityCount > 0) {
                form.pass();
                equalOpportunityCount--;
            } else if (form.getType().isSocietyDiversity() && societyDiversityCount > 0) {
                form.pass();
                societyDiversityCount--;
            } else {
                form.fail();
            }
        }

        for (Form form : meisterTalentFormList) {
            if (meisterTalentCount > 0) {
                form.pass();
                meisterTalentCount--;
            } else {
                form.fail();
            }
        }

        formRepository.flush();
        List<Form> regularFormList = formRepository.findFirstPassedRegularForm();

        for (Form form : regularFormList) {
            if (regularCount > 0) {
                form.pass();
                regularCount--;
            } else {
                form.fail();
            }
        }

        formRepository.flush();
        List<Form> supernumeraryFormList = formRepository.findFirstPassedSupernumeraryForm();

        for (Form form : supernumeraryFormList) {
            if (form.getType().isNationalVeteransEducation() && nationalVeteransEducationCount > 0) {
                form.pass();
                nationalVeteransEducationCount--;
            } else if (form.getType().isSpecialAdmission() && specialAdmissionCount > 0) {
                form.pass();
                specialAdmissionCount--;
            } else {
                form.fail();
            }
        }
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
