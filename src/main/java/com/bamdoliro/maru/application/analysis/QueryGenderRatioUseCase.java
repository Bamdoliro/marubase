package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.type.Gender;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.analysis.dto.request.GenderRatioRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.GenderRatioResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@UseCase
public class QueryGenderRatioUseCase {
    private final FormRepository formRepository;

    public List<GenderRatioResponse> execute(GenderRatioRequest request) {
        List<GenderRatioResponse> result = new ArrayList<>();

        List<FormType.Category> subCategories = new ArrayList<>();

        if (request.getMainCategory().equals(FormType.Category.REGULAR))
            subCategories.add(FormType.Category.REGULAR);
        else if (request.getMainCategory().equals(FormType.Category.SPECIAL))
            subCategories.addAll(List.of(FormType.Category.EQUAL_OPPORTUNITY, FormType.Category.SOCIETY_DIVERSITY));
        else subCategories.add(FormType.Category.SUPERNUMERARY);

        Map<FormType.Category, List<Form>> formLists = subCategories.stream()
                .collect(Collectors.toMap(
                        category -> category,
                        category -> formRepository.findByCategory(category).stream()
                                .filter(form -> request.getStatusList().contains(form.getStatus()))
                                .collect(Collectors.toList())
                ));

        for(Map.Entry<FormType.Category, List<Form>> entry : formLists.entrySet()) {
            FormType.Category category = entry.getKey();
            List<Form> formList = entry.getValue();

            long busanMale = formList.stream().filter(this::isBusan).filter(this::isMale).count();
            long busanFeMale = formList.stream().filter(this::isBusan).filter(this::isFemale).count();
            long otherLocationMale = formList.stream().filter(this::isNotBusan).filter(this::isMale).count();
            long otherLocationFemale = formList.stream().filter(this::isNotBusan).filter(this::isFemale).count();

            result.add(new GenderRatioResponse(category, busanMale, busanFeMale, otherLocationMale, otherLocationFemale));
        }

        return result;
    }

    private boolean isBusan(Form form) {
        return form.getEducation().getSchool().isBusan();
    }

    private boolean isNotBusan(Form form) {
        return !isBusan(form);
    }

    private boolean isMale(Form form) {
        return form.getApplicant().getGender().equals(Gender.MALE);
    }

    private boolean isFemale(Form form) {
        return !isMale(form);
    }
}