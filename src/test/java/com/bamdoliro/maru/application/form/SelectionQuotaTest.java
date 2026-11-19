package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import java.util.function.IntUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionQuotaTest {

    private static final IntUnaryOperator FIRST_ROUND = count -> (int) Math.ceil(count * FixedNumber.MULTIPLE);
    private static final IntUnaryOperator SECOND_ROUND = IntUnaryOperator.identity();

    @Test
    void 변경된_배수와_타지역_비율을_각_단계_반올림_규칙대로_계산한다() {
        var policy = new SelectionPolicy(40, 8, 4, 2, 3, new BigDecimal("1.25"), new BigDecimal("0.35"));
        assertEquals(65, policy.firstRoundCount(policy.total()));
        assertEquals(22, policy.otherRegionCount(true));
        assertEquals(19, policy.otherRegionCount(false));
        SelectionQuota first = SelectionQuota.of(policy, 20, 20, 20, policy::firstRoundCount);
        SelectionQuota second = SelectionQuota.of(policy, 20, 20, 20, SECOND_ROUND);
        assertQuota(50, 10, 2, 3, first);
        assertQuota(40, 8, 2, 2, second);
        assertEquals(3, first.nationalVeteransEducation());
        assertEquals(4, first.specialAdmission());
    }

    @Test
    void 기회균등이_미달이면_남는_자리를_사회다양성으로_넘긴다() {
        // when
        SelectionQuota first = SelectionQuota.of(13, 1, 6, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 1, 6, SECOND_ROUND);

        // then
        // 1차 사회통합 8석 → 기회균등 4 / 사회다양성 4 → 기회균등 미달 3석을 사회다양성으로 넘겨 7석이 되지만,
        // 사회다양성 지원자가 6명이므로 6명으로 낮춘다. 남는 1석은 원 정원(6) 기준 미달이 아니라 일반전형으로 넘기지 않는다.
        assertQuota(63, 13, 1, 6, first);
        assertQuota(48, 10, 1, 5, second);
    }

    @Test
    void 기회균등_지원자가_없으면_사회다양성이_사회통합_정원을_모두_가져간다() {
        // when
        SelectionQuota first = SelectionQuota.of(13, 0, 20, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 0, 20, SECOND_ROUND);

        // then
        assertQuota(63, 13, 0, 8, first);
        assertQuota(48, 10, 0, 6, second);
    }

    @Test
    void 사회다양성이_미달이면_남는_자리를_기회균등으로_넘긴다() {
        // when
        SelectionQuota first = SelectionQuota.of(13, 20, 1, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 20, 1, SECOND_ROUND);

        // then
        assertQuota(63, 13, 7, 1, first);
        assertQuota(48, 10, 5, 1, second);
    }

    @Test
    void 사회통합_전체가_미달이면_남는_자리만큼_일반전형_정원을_늘린다() {
        // when
        SelectionQuota first = SelectionQuota.of(13, 1, 2, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 1, 2, SECOND_ROUND);

        // then
        // 1차: 일반 ceil((48 + 3) * 1.3) = 67
        assertQuota(67, 13, 1, 2, first);
        assertQuota(51, 10, 1, 2, second);
    }

    @Test
    void 사회통합_지원자가_없으면_사회통합_정원_전부를_일반전형으로_넘긴다() {
        // when
        SelectionQuota first = SelectionQuota.of(13, 0, 0, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 0, 0, SECOND_ROUND);

        // then
        // 1차: 일반 ceil((48 + 6) * 1.3) = 71
        assertQuota(71, 13, 0, 0, first);
        assertQuota(54, 10, 0, 0, second);
    }

    @Test
    void 양쪽_모두_정원_이상이면_반반_선발한다() {
        // when
        SelectionQuota first = SelectionQuota.of(13, 10, 10, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 10, 10, SECOND_ROUND);

        // then
        assertQuota(63, 13, 4, 4, first);
        assertQuota(48, 10, 3, 3, second);
    }

    @Test
    void 마이스터인재전형이_미달이면_일반전형_정원을_늘린다() {
        // when
        SelectionQuota first = SelectionQuota.of(5, 10, 10, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(5, 10, 10, SECOND_ROUND);

        // then
        // 1차: 일반 ceil((48 + 5) * 1.3) = 69, 마이스터 ceil(5 * 1.3) = 7
        assertQuota(69, 7, 4, 4, first);
        assertQuota(53, 5, 3, 3, second);
    }

    @Test
    void 사회통합_선발_인원이_홀수여도_하위_분류_합이_선발_인원을_넘지_않는다() {
        // when
        // 사회통합 지원자 5명 → 원 정원 5 → 1차 ceil(5 * 1.3) = 7
        SelectionQuota first = SelectionQuota.of(13, 3, 2, FIRST_ROUND);
        SelectionQuota second = SelectionQuota.of(10, 3, 2, SECOND_ROUND);

        // then
        // 1차: 일반 ceil((48 + 1) * 1.3) = 64, 사회통합 7석 → 기회균등 3 / 사회다양성 4 → 사회다양성은 지원자 2명
        assertQuota(64, 13, 3, 2, first);
        assertQuota(49, 10, 3, 2, second);
    }

    @Test
    void 하위_분류_합은_어떤_지원자_조합에서도_사회통합_선발_인원과_지원자_수를_넘지_않는다() {
        for (int equalOpportunity = 0; equalOpportunity <= 20; equalOpportunity++) {
            for (int societyDiversity = 0; societyDiversity <= 20; societyDiversity++) {
                for (IntUnaryOperator multiple : new IntUnaryOperator[]{FIRST_ROUND, SECOND_ROUND}) {
                    SelectionQuota quota = SelectionQuota.of(13, equalOpportunity, societyDiversity, multiple);
                    int applicantCount = equalOpportunity + societyDiversity;
                    int socialIntegrationCount = multiple.applyAsInt(Math.min(applicantCount, FixedNumber.SOCIAL_INTEGRATION));
                    int selectedCount = quota.equalOpportunity() + quota.societyDiversity();

                    assertTrue(quota.equalOpportunity() <= equalOpportunity);
                    assertTrue(quota.societyDiversity() <= societyDiversity);
                    assertEquals(Math.min(applicantCount, socialIntegrationCount), selectedCount);
                }
            }
        }
    }

    private void assertQuota(int regular, int meisterTalent, int equalOpportunity, int societyDiversity, SelectionQuota quota) {
        assertEquals(regular, quota.regular());
        assertEquals(meisterTalent, quota.meisterTalent());
        assertEquals(equalOpportunity, quota.equalOpportunity());
        assertEquals(societyDiversity, quota.societyDiversity());
    }
}
