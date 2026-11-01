package com.bamdoliro.maru.domain.policy;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectList;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.policy.formula.ScoreFormulaPreview;
import com.bamdoliro.maru.domain.policy.formula.ScoreSubject;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.util.MathUtil;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/** 합성 데이터만 사용한다. 운영 학생/DB 조회 없이 기존 계산기와 교과 결과를 비교한다. */
class SubjectFormulaRegressionTest {
    private final ScoreFormulaPreview preview = new ScoreFormulaPreview();
    private final CalculateFormScoreService legacy = new CalculateFormScoreService();

    @Test
    void 학교성적_F_정보_학기_수학가중치가_기존교과계산과_일치한다() throws Exception {
        String source = Files.readString(Path.of("examples/score-policy/current-subjects.dsl"));
        Random random = new Random(447);
        for (int scenario = 0; scenario < 40; scenario++) {
            List<Subject> subjects = new ArrayList<>();
            for (int grade = 1; grade <= 3; grade++) {
                for (int semester = 1; semester <= 2; semester++) {
                    for (String name : new String[]{"국어", "영어", "수학", "과학", "음악", "정보"}) {
                        if (random.nextInt(5) != 0) {
                            subjects.add(new Subject(grade, semester, name,
                                    AchievementLevel.values()[random.nextInt(6)]));
                        }
                    }
                }
            }
            for (FormType type : new FormType[]{FormType.REGULAR, FormType.MEISTER_TALENT}) {
                Form form = FormFixture.createForm(type);
                compare(source, form, subjects, false);
            }
        }
    }

    @Test
    void 검정고시_환산_경계값도_기존교과계산과_일치한다() throws Exception {
        String source = Files.readString(Path.of("examples/score-policy/ged-subjects.dsl"));
        for (int score : new int[]{0, 88, 89, 92, 93, 96, 97, 98, 99, 100}) {
            List<Subject> subjects = List.of(
                    new Subject("국어", score), new Subject("수학", 99), new Subject("영어", 97));
            for (FormType type : new FormType[]{FormType.REGULAR, FormType.MEISTER_TALENT}) {
                compare(source, FormFixture.createQualificationExaminationForm(type), subjects, true);
            }
        }
    }

    private void compare(String source, Form form, List<Subject> subjects, boolean ged) {
        ReflectionTestUtils.setField(form.getGrade(), "subjectList", SubjectList.of(subjects));
        List<ScoreSubject> raw = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            raw.add(new ScoreSubject("s" + i, ged ? null : subject.getGrade(),
                    ged ? null : subject.getSemester(), subject.getSubjectName(),
                    ged ? null : subject.getAchievementLevel().name(),
                    ged ? BigDecimal.valueOf(subject.getOriginalScore()) : null));
        }
        var result = preview.execute(source, Map.of("일반전형",
                form.getType().isRegular() ? BigDecimal.ONE : BigDecimal.ZERO), raw);
        assertThat(result.valid()).as(result.error()).isTrue();
        BigDecimal expected = BigDecimal.valueOf(MathUtil.roundTo(legacy.calculateSubjectGradeScore(form), 3));
        assertThat(result.outputs().get("교과")).isEqualByComparingTo(expected);
    }
}
