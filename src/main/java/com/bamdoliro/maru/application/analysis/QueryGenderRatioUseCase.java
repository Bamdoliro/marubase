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
        FormType.Category mainCategory = request.getMainCategory();
        List<GenderRatioResponse> result = new ArrayList<>();
        List<FormType.Category> subCategories = new ArrayList<>();

        if (mainCategory.equals(FormType.Category.REGULAR))
            subCategories.add(FormType.Category.REGULAR);
        else if (mainCategory.equals(FormType.Category.SPECIAL))
            subCategories.addAll(List.of(
                    FormType.Category.MEISTER_TALENT,
                    FormType.Category.SOCIAL_INTEGRATION));
        else if (mainCategory.equals(FormType.Category.SUPERNUMERARY))
            subCategories.addAll(List.of(
                    FormType.Category.NATIONAL_VETERANS_EDUCATION,
                    FormType.Category.SPECIAL_ADMISSION));

        Map<FormType.Category, List<Form>> formLists = subCategories.stream()
                .collect(Collectors.toMap(
                        category -> category,
                        category -> {
                            if (request.getType().equals("ORIGINAL")) {
                                return formRepository.findByOriginalCategory(category).stream()
                                        .filter(form -> request.getStatusList().contains(form.getStatus()))
                                        .toList();
                            }
                            return formRepository.findByCategory(category).stream()
                                    .filter(form -> request.getStatusList().contains(form.getStatus()))
                                    .toList();
                        }
                ));

        for(Map.Entry<FormType.Category, List<Form>> entry : formLists.entrySet()) {
            FormType.Category category = entry.getKey();
            List<Form> formList = entry.getValue();
            Map<Boolean, Map<Gender, Long>> grouped = formList.stream()
                    .collect(Collectors.groupingBy(
                            this::isBusan,
                            Collectors.groupingBy(
                                    form -> form.getApplicant().getGender(),
                                    Collectors.counting()
                            )
                    ));

            long busanMale = grouped.getOrDefault(true, Map.of()).getOrDefault(Gender.MALE, 0L);
            long busanFemale = grouped.getOrDefault(true, Map.of()).getOrDefault(Gender.FEMALE, 0L);
            long otherLocationMale = grouped.getOrDefault(false, Map.of()).getOrDefault(Gender.MALE, 0L);
            long otherLocationFemale = grouped.getOrDefault(false, Map.of()).getOrDefault(Gender.FEMALE, 0L);

            result.add(new GenderRatioResponse(category, busanMale, busanFemale, otherLocationMale, otherLocationFemale));
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
        return form.getApplicant().getGender() == (Gender.MALE);
    }

    private boolean isFemale(Form form) {
        return form.getApplicant().getGender() == (Gender.FEMALE);
    }
}