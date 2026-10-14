package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;

import java.util.List;
import java.util.function.Predicate;

/**
 * 전형별 지원자 수를 지정해 선발 시나리오를 구성한다.
 * 동점자 추가 합격이 결과를 흔들지 않도록 1차 점수를 서로 겹치지 않게 지정한다.
 * 일반전형 지원자는 특별전형에서 일반전형으로 전환된 지원자보다 항상 점수가 높다.
 */
record SelectionScenarioSupport(
        UserRepository userRepository,
        FormRepository formRepository,
        AssignExaminationNumberService assignExaminationNumberService,
        CalculateFormScoreService calculateFormScoreService
) {

    static final int REGULAR_APPLICANT_COUNT = 100;
    private static final double REGULAR_TOP_SCORE = 100_000;
    private static final double SPECIAL_TOP_SCORE = 50_000;
    // 출석·봉사·가산점 합계(최대 40점)보다 넓게 벌려 1차 점수가 겹치지 않게 한다.
    private static final double SCORE_STEP = 100;

    void saveApprovedForms(int meisterTalent, int equalOpportunity, int societyDiversity) {
        saveApprovedForms(FormType.REGULAR, REGULAR_APPLICANT_COUNT, REGULAR_TOP_SCORE);
        saveApprovedForms(FormType.MEISTER_TALENT, meisterTalent, SPECIAL_TOP_SCORE);
        saveApprovedForms(FormType.ONE_PARENT, equalOpportunity, SPECIAL_TOP_SCORE);
        saveApprovedForms(FormType.MULTI_CHILDREN, societyDiversity, SPECIAL_TOP_SCORE);
    }

    private void saveApprovedForms(FormType type, int count, double topScore) {
        List<User> userList = userRepository.saveAll(UserFixture.generateUserList(count));
        for (int i = 0; i < count; i++) {
            Form form = FormFixture.createRandomBusanForm(userList.get(i));
            form.update(form.getApplicant(), form.getParent(), form.getEducation(), form.getGrade(), form.getDocument(), type);
            assignExaminationNumberService.execute(form);
            form.approve();
            calculateFormScoreService.execute(form);
            form.getScore().updateSubjectScore(topScore - i * SCORE_STEP);
            formRepository.save(form);
        }
    }

    void updateSecondRoundScoreToFirstRoundScore() {
        formRepository.findByStatus(FormStatus.FIRST_PASSED).forEach(form -> {
            if (form.getType().isMeister()) {
                form.getScore().updateSecondRoundMeisterScore(0.0, 0.0, 0.0);
            } else {
                form.getScore().updateSecondRoundScore(0.0, 0.0);
            }
            formRepository.save(form);
        });
    }

    String countPassedForms(Predicate<Form> isPassed) {
        List<Form> passedFormList = formRepository.findAll().stream()
                .filter(isPassed)
                .toList();

        return String.format(
                "regular=%d, meisterTalent=%d, equalOpportunity=%d, societyDiversity=%d",
                count(passedFormList, form -> form.getType() == FormType.REGULAR),
                count(passedFormList, form -> form.getType().isMeister()),
                count(passedFormList, form -> form.getType().isEqualOpportunity()),
                count(passedFormList, form -> form.getType().isSocietyDiversity())
        );
    }

    private long count(List<Form> formList, Predicate<Form> filter) {
        return formList.stream().filter(filter).count();
    }
}
