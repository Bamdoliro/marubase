package com.bamdoliro.maru.domain.form.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum FormType implements EnumProperty {

    REGULAR("일반전형", Category.REGULAR, null, null),

    MEISTER_TALENT("마이스터인재전형", Category.SPECIAL, Category.MEISTER_TALENT, null),

    NATIONAL_BASIC_LIVING("국가기초생활수급권자", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.EQUAL_OPPORTUNITY),
    NEAR_POVERTY("차상위계층", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.EQUAL_OPPORTUNITY),
    NATIONAL_VETERANS("국가보훈자녀", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.EQUAL_OPPORTUNITY),
    ONE_PARENT("한부모가정", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.EQUAL_OPPORTUNITY),

    FROM_NORTH_KOREA("북한이탈청소년", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.SOCIETY_DIVERSITY),
    MULTICULTURAL("다문화가정", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.SOCIETY_DIVERSITY),
    TEEN_HOUSEHOLDER("소년소녀가장", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.SOCIETY_DIVERSITY),
    MULTI_CHILDREN("다자녀가정자녀", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.SOCIETY_DIVERSITY),
    FARMING_AND_FISHING("농어촌지역출신자", Category.SPECIAL, Category.SOCIAL_INTEGRATION, Category.SOCIETY_DIVERSITY),

    NATIONAL_VETERANS_EDUCATION("국가보훈대상자 중 교육지원대상자녀", Category.SUPERNUMERARY, Category.NATIONAL_VETERANS_EDUCATION, null),
    SPECIAL_ADMISSION("특례입학대상자", Category.SUPERNUMERARY, Category.SPECIAL_ADMISSION, null),
    ;


    private final String description;
    private final Category mainCategory;
    private final Category middleCategory;
    private final Category subCategory;

    @Getter
    @RequiredArgsConstructor
    public enum Category implements EnumProperty {
        // Main Category
        REGULAR("일반전형"),
        SPECIAL("특별전형"),
        SUPERNUMERARY("정원 외 전형"),

        // Middle Category
        SOCIAL_INTEGRATION("사회통합전형"),
        MEISTER_TALENT("마이스터인재전형"),
        NATIONAL_VETERANS_EDUCATION("국가보훈대상자 중 교육지원대상자녀"),
        SPECIAL_ADMISSION("특례입학대상자"),

        // Sub Category
        EQUAL_OPPORTUNITY("기회균등전형"),
        SOCIETY_DIVERSITY("사회다양성전형");

        private final String description;

        public static Category valueOfDescription(String description) {
            for (Category category : Category.values()) {
                if (Objects.equals(category.getDescription(), description)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("No matching constant for [" + description + "]");
        }
    }

    public boolean isRegular() {
        return mainCategory == Category.REGULAR;
    }

    public boolean isRegularOrSupernumerary() {
        return isRegular() || isSupernumerary();
    }

    public boolean isSpecial() {
        return mainCategory == Category.SPECIAL;
    }

    public boolean isMeister() {
        return this == MEISTER_TALENT;
    }

    public boolean isSocial() {
        return middleCategory == Category.SOCIAL_INTEGRATION;
    }

    public boolean isEqualOpportunity() {
        return subCategory == Category.EQUAL_OPPORTUNITY;
    }

    public boolean isSocietyDiversity() {
        return subCategory == Category.SOCIETY_DIVERSITY;
    }

    public boolean isSupernumerary() {
        return mainCategory == Category.SUPERNUMERARY;
    }

    public boolean isNationalVeteransEducation() {
        return this == NATIONAL_VETERANS_EDUCATION;
    }

    public boolean isSpecialAdmission() {
        return this == SPECIAL_ADMISSION;
    }

    public boolean categoryEquals(Category category) {
        return mainCategory.equals(category)
                || (Objects.nonNull(middleCategory) && middleCategory.equals(category))
                || (Objects.nonNull(subCategory) && subCategory.equals(category));
    }

    public FormType.Category getCategory() {
        if (isSpecial()) {
            return middleCategory;
        }

        return mainCategory;
    }
}
