package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.NumberOfApplicantsVo;
import com.bamdoliro.maru.presentation.analysis.dto.response.GenderRatioResponse;
import com.bamdoliro.maru.presentation.analysis.dto.response.GradeDistributionResponse;
import com.bamdoliro.maru.presentation.analysis.dto.response.NumberOfApplicantsResponse;
import com.bamdoliro.maru.presentation.analysis.dto.response.SchoolStatusResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.bamdoliro.maru.shared.util.RandomUtil.randomDouble;
import static com.bamdoliro.maru.shared.util.RandomUtil.randomNumber;

public class AnalysisFixture {

    public static List<NumberOfApplicantsResponse> createNumberOfApplicantsResponseList() {
        List<NumberOfApplicantsResponse> responseList = new ArrayList<>();
        for (FormType formType : FormType.values()) {
            responseList.add(new NumberOfApplicantsResponse(formType, (long)randomNumber(0, 50)));
        }
        return responseList;
    }

    public static List<GradeDistributionResponse> createGradeDistributionResponseList() {
        List<GradeDistributionResponse> responseList = new ArrayList<>();
        for (FormType formType : FormType.values()) {
            responseList.add(new GradeDistributionResponse(formType, randomDouble(0.0, 240.0), randomDouble(0.0, 240.0), randomDouble(0.0, 240.0), randomDouble(0.0, 240.0), randomDouble(0.0, 240.0), randomDouble(0.0, 240.0)));
        }
        return responseList;
    }

    public static List<GenderRatioResponse> createGenderRatioResponse(FormType.Category mainCategory) {
        if (mainCategory.equals(FormType.Category.REGULAR)) {
            return List.of(
                    new GenderRatioResponse(
                            FormType.Category.REGULAR,
                            randomNumber(0, 30),
                            randomNumber(0, 15),
                            randomNumber(0, 30),
                            randomNumber(0, 15)
                    )
            );
        } else if (mainCategory.equals(FormType.Category.SPECIAL)) {
            return List.of(
                    new GenderRatioResponse(
                            FormType.Category.MEISTER_TALENT,
                            randomNumber(0, 30),
                            randomNumber(0, 15),
                            randomNumber(0, 30),
                            randomNumber(0, 15)
                    ),
                    new GenderRatioResponse(FormType.Category.SOCIAL_INTEGRATION,
                            randomNumber(0, 30),
                            randomNumber(0, 15),
                            randomNumber(0, 30),
                            randomNumber(0, 15)
                    ),
                    new GenderRatioResponse(
                            FormType.Category.SUPERNUMERARY,
                            randomNumber(0, 30),
                            randomNumber(0, 15),
                            randomNumber(0, 30),
                            randomNumber(0, 15)
                    )
            );
        } else {
            return List.of(
                    new GenderRatioResponse(
                            FormType.Category.SUPERNUMERARY,
                            randomNumber(0, 30),
                            randomNumber(0, 15),
                            randomNumber(0, 30),
                            randomNumber(0, 15)
                    )
            );
        }
    }

    public static List<SchoolStatusResponse> createSchoolStatusResponse(List<String> isBusan, List<String> gu) {
        if (Objects.equals(isBusan.get(0), "true")) {
            if (gu.get(0) == null) {
                return List.of(
                        new SchoolStatusResponse(
                                "김밤돌",
                                "신라중학교",
                                "부산광역시 사상구 백양대로700번길 35-8"
                        ),
                        new SchoolStatusResponse(
                                "김이로",
                                "성동중학교",
                                "부산광역시 남구 남동천로 38"
                        ),
                        new SchoolStatusResponse(
                                "금곰돌",
                                "가락중학교",
                                "부산광역시 강서구 가락대로 1405")
                );
            }
            return List.of(
                    new SchoolStatusResponse(
                            "김밤돌",
                            "신라중학교",
                            "부산광역시 사상구 백양대로700번길 35-8"
                    )
            );
        }
        return List.of(
                new SchoolStatusResponse(
                        "김밤돌",
                        "비전중학교",
                        "경기도 비전시 비전구 비전로 1"
                )
        );
    }

    public static NumberOfApplicantsVo createNumberOfApplicantsVo() {
        return new NumberOfApplicantsVo(
                randomFormType(),
                (long) randomNumber(0, 50)
        );
    }

    private static FormType randomFormType() {
        FormType[] values = {FormType.REGULAR, FormType.REGULAR, FormType.REGULAR, FormType.REGULAR, FormType.MEISTER_TALENT, FormType.MEISTER_TALENT, FormType.MEISTER_TALENT, FormType.MEISTER_TALENT, FormType.ONE_PARENT, FormType.MULTI_CHILDREN, FormType.SPECIAL_ADMISSION};
        return values[new Random().nextInt(values.length)];
    }
}
