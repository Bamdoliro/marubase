package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.shared.constants.FixedNumber;

import java.util.function.IntUnaryOperator;

/**
 * 1차·2차 선발에서 공용으로 쓰는 전형별 선발 인원.
 * 마이스터인재·사회통합전형의 미달분은 배수를 적용하기 전 원 정원 단위로 일반전형에 이월하고,
 * 사회통합전형의 하위 분류(기회균등·사회다양성) 분할은 배수를 적용한 실제 선발 인원으로 한다.
 */
record SelectionQuota(
        int regular,
        int meisterTalent,
        int equalOpportunity,
        int societyDiversity,
        int nationalVeteransEducation,
        int specialAdmission
) {

    static SelectionQuota of(
            int meisterTalentApplicantCount,
            int equalOpportunityApplicantCount,
            int societyDiversityApplicantCount,
            IntUnaryOperator multiple
    ) {
        int regular = FixedNumber.REGULAR;
        int meisterTalent = FixedNumber.MEISTER_TALENT;
        int socialIntegration = FixedNumber.SOCIAL_INTEGRATION;

        if (meisterTalentApplicantCount < FixedNumber.MEISTER_TALENT) {
            int gap = FixedNumber.MEISTER_TALENT - meisterTalentApplicantCount;
            regular += gap;
            meisterTalent -= gap;
        }

        int socialIntegrationApplicantCount = equalOpportunityApplicantCount + societyDiversityApplicantCount;
        if (socialIntegrationApplicantCount < FixedNumber.SOCIAL_INTEGRATION) {
            int gap = FixedNumber.SOCIAL_INTEGRATION - socialIntegrationApplicantCount;
            regular += gap;
            socialIntegration -= gap;
        }

        int socialIntegrationCount = multiple.applyAsInt(socialIntegration);
        int equalOpportunity = socialIntegrationCount / 2;
        int societyDiversity = socialIntegrationCount - equalOpportunity;

        if (equalOpportunityApplicantCount < equalOpportunity) {
            societyDiversity += equalOpportunity - equalOpportunityApplicantCount;
            equalOpportunity = equalOpportunityApplicantCount;
        }

        if (societyDiversityApplicantCount < societyDiversity) {
            int gap = societyDiversity - societyDiversityApplicantCount;
            equalOpportunity += Math.min(gap, equalOpportunityApplicantCount - equalOpportunity);
            societyDiversity = societyDiversityApplicantCount;
        }

        return new SelectionQuota(
                multiple.applyAsInt(regular),
                multiple.applyAsInt(meisterTalent),
                equalOpportunity,
                societyDiversity,
                multiple.applyAsInt(FixedNumber.NATIONAL_VETERANS_EDUCATION),
                multiple.applyAsInt(FixedNumber.SPECIAL_ADMISSION)
        );
    }
}
