package com.bamdoliro.maru.domain.policy;

import com.bamdoliro.maru.domain.policy.formula.ScoreFormulaPreview;
import com.bamdoliro.maru.domain.policy.formula.ScoreSubject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SubjectFormulaPreviewTest {
    private final ScoreFormulaPreview preview = new ScoreFormulaPreview();

    private ScoreSubject subject(String id, int grade, int semester, String name, String level) {
        return new ScoreSubject(id, grade, semester, name, level, null);
    }

    private List<ScoreSubject> sample() {
        return List.of(
                subject("math", 2, 1, "수학", "F"),
                subject("korean", 2, 1, "국어", "A"),
                subject("science", 2, 1, "과학", "F"),
                subject("excluded", 3, 2, "수학", "A"));
    }

    @Test
    void F대체_제외_환산_가중집계를_명시하고_원본을_보존한다() throws Exception {
        var result = preview.execute("""
                계산 대상 = 선택(과목들,
                    학년 == 2 && 학기 == 1
                    && (성취 != "F" || 포함(과목명, "국어", "영어", "수학")) # 비핵심 F 제외
                )
                계산 대체 = 성취변경(대상, 조건(성취 == "F", "C", 성취))
                계산 환산 = 점수화(대체,
                    대응(성취, "A", 5, "B", 4, "C", 3, "D", 2, "E", 1),
                    조건(과목명 == "수학", 2, 1)
                )
                출력 반영수 = 개수(환산)
                출력 분모 = 합계(환산, 가중치)
                출력 점수합 = 합계(환산, 점수)
                출력 단순평균 = 평균(환산, 점수)
                출력 교과 = 가중평균(환산, 점수, 가중치)
                """, Map.of(), sample());
        assertThat(result.valid()).as(result.error()).isTrue();
        assertThat(result.outputs().get("반영수")).isEqualByComparingTo("2");
        assertThat(result.outputs().get("분모")).isEqualByComparingTo("3");
        assertThat(result.outputs().get("점수합")).isEqualByComparingTo("8");
        assertThat(result.outputs().get("단순평균")).isEqualByComparingTo("4");
        assertThat(result.outputs().get("교과")).isEqualByComparingTo("3.666666666667");
        var trace = new ObjectMapper().valueToTree(result.trace());
        assertThat(trace.get("대체").get(0).get("original").get("achievement").asText()).isEqualTo("F");
        assertThat(trace.get("대체").get(0).get("achievement").asText()).isEqualTo("C");
        assertThat(trace.get("대상").get(0).get("achievement").asText()).isEqualTo("F");
        assertThat(trace.get("환산").get(0).get("weight").decimalValue()).isEqualByComparingTo("2");
    }

    @Test
    void F의_0점_포함과_제외는_다르다() {
        var result = preview.execute("""
                계산 전체 = 점수화(과목들, 대응(성취, "F", 0, "A", 5), 1)
                계산 제외 = 선택(전체, 성취 != "F")
                출력 포함평균 = 평균(전체, 점수)
                출력 제외평균 = 평균(제외, 점수)
                """, Map.of(), List.of(subject("1", 2, 1, "과학", "F"), subject("2", 2, 1, "국어", "A")));
        assertThat(result.valid()).as(result.error()).isTrue();
        assertThat(result.outputs().get("포함평균")).isEqualByComparingTo("2.5");
        assertThat(result.outputs().get("제외평균")).isEqualByComparingTo("5");
    }

    @Test
    void 빈목록과_누락값은_명시한_조건으로만_대체한다() {
        assertThat(preview.execute("출력 값 = 평균(과목들, 원점수)", Map.of()).valid()).isFalse();
        var empty = preview.execute("""
                출력 합 = 합계(과목들, 원점수)
                출력 평균값 = 조건(개수(과목들) == 0, 3, 평균(과목들, 원점수))
                """, Map.of());
        assertThat(empty.outputs().get("합")).isEqualByComparingTo("0");
        assertThat(empty.outputs().get("평균값")).isEqualByComparingTo("3");
        var missing = preview.execute("""
                출력 합 = 합계(과목들, 조건(있음(원점수), 원점수, 7))
                출력 누락수 = 개수(선택(과목들, 원점수 == 없음))
                """, Map.of(), List.of(subject("1", 2, 1, "국어", "A"),
                new ScoreSubject("2", null, null, "수학", null, BigDecimal.ZERO)));
        assertThat(missing.valid()).as(missing.error()).isTrue();
        assertThat(missing.outputs().get("합")).isEqualByComparingTo("7");
        assertThat(missing.outputs().get("누락수")).isEqualByComparingTo("1");
    }

    @Test
    void 구간_환산은_하한을_포함하며_원점수를_보존한다() throws Exception {
        int[] scores = {100, 99, 98, 97, 96, 93, 92, 89, 88, 0};
        int[] expected = {5, 5, 4, 4, 3, 3, 2, 2, 1, 1};
        for (int i = 0; i < scores.length; i++) {
            var result = preview.execute("""
                    계산 환산 = 성취변경(과목들, 구간(원점수, 99, "A", 97, "B", 93, "C", 89, "D", "E"))
                    출력 점 = 합계(환산, 대응(성취, "A", 5, "B", 4, "C", 3, "D", 2, "E", 1))
                    """, Map.of(), List.of(new ScoreSubject("ged", null, null, "수학", null, BigDecimal.valueOf(scores[i]))));
            assertThat(result.valid()).as(result.error()).isTrue();
            assertThat(result.outputs().get("점")).isEqualByComparingTo(BigDecimal.valueOf(expected[i]));
            var trace = new ObjectMapper().valueToTree(result.trace());
            assertThat(trace.get("환산").get(0).get("original").get("originalScore").asInt()).isEqualTo(scores[i]);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "출력 값 = 합계(과목들, 성취)",
            "출력 값 = 조건(1, 3, 합계(과목들, 성취))",
            "출력 값 = 개수(선택(과목들, 성취))",
            "출력 값 = 가중평균(과목들, 5, -1)",
            "출력 값 = 가중평균(과목들, 5, 0)",
            "출력 값 = 합계(과목들, 원점수)",
            "출력 값 = 과목명",
            "출력 값 = 1 + \"2\"",
            "출력 값 = 대응(\"F\", \"A\", 5)",
            "출력 값 = 대응(\"A\", \"A\", 5, \"A\", 4)",
            "출력 값 = 구간(90, 80, 4, 90, 5, 1)",
            "출력 값 = 구간(90, 90, 4, 90, 5, 1)",
            "출력 값 = 개수(성취변경(과목들, \"Z\"))",
            "출력 값 = 대응(\"A\", \"A\", 5, \"B\", \"4\")",
            "출력 값 = 선택(과목들, 1)",
            "출력 값 = 합계(과목들, 알수없는필드)",
            "출력 값 = 선택(과목들)",
            "출력 값 = 개수(1)"
    })
    void 잘못된_식은_부분결과_없이_실패한다(String source) {
        var result = preview.execute(source, Map.of(), sample());
        assertThat(result.valid()).as(source).isFalse();
        assertThat(result.trace()).isEmpty();
        assertThat(result.outputs()).isEmpty();
    }

    @Test
    void 빈목록에서도_타입을_검증하고_성취변경은_기존점수를_폐기한다() {
        assertThat(preview.execute("출력 값 = 합계(과목들, 성취)", Map.of()).valid()).isFalse();
        var result = preview.execute("""
                계산 환산 = 점수화(과목들, 5, 1)
                계산 수정 = 성취변경(환산, "C")
                출력 합 = 합계(수정, 점수)
                """, Map.of(), sample());
        assertThat(result.valid()).isFalse();
    }

    @Test
    void 입력_과목의_범위와_식별자를_검증한다() {
        assertThat(preview.execute("출력 값 = 개수(과목들)", Map.of(), List.of(sample().getFirst(), sample().getFirst())).valid()).isFalse();
        for (ScoreSubject invalid : List.of(
                new ScoreSubject("x", 4, 1, "국어", "A", null),
                new ScoreSubject("x", 2, null, "국어", "A", null),
                new ScoreSubject("x", 2, 1, "국어", "Z", null),
                new ScoreSubject("x", null, null, "국어", null, null),
                new ScoreSubject("x", null, null, "국어", null, new BigDecimal("101")))) {
            assertThat(preview.execute("출력 값 = 개수(과목들)", Map.of(), List.of(invalid)).valid()).isFalse();
        }
    }

    @Test
    void 중첩_집계와_입력_출력의_한도를_검증한다() {
        List<ScoreSubject> rows = new ArrayList<>();
        for (int i = 0; i < 240; i++) {
            rows.add(subject("s" + i, 2, 1, "수학", "A"));
        }
        var heavy = preview.execute("출력 값 = 합계(과목들, 합계(과목들, 1))", Map.of(), rows);
        assertThat(heavy.valid()).isFalse();
        assertThat(heavy.error()).contains("계산 한도");
        StringBuilder source = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            source.append("계산 목록").append(i).append(" = 선택(과목들, 1)\n");
        }
        source.append("출력 값 = 1");
        var trace = preview.execute(source.toString(), Map.of(), rows);
        assertThat(trace.valid()).isFalse();
        assertThat(trace.error()).contains("2048");
        rows.add(subject("too-many", 2, 1, "수학", "A"));
        assertThat(preview.execute("출력 값 = 개수(과목들)", Map.of(), rows).valid()).isFalse();
    }

    @Test
    void 전역입력과_행필드의_이름이_같아도_범위를_구분한다() {
        var result = preview.execute("""
                출력 전역 = 원점수
                출력 행합 = 합계(과목들, 원점수)
                """, Map.of("원점수", new BigDecimal("99")),
                List.of(new ScoreSubject("ged", null, null, "국어", null, new BigDecimal("80"))));
        assertThat(result.outputs().get("전역")).isEqualByComparingTo("99");
        assertThat(result.outputs().get("행합")).isEqualByComparingTo("80");
    }

    @Test
    void 학교와_검정고시_예제가_동작한다() throws Exception {
        var school = preview.execute(Files.readString(Path.of("examples/score-policy/current-subjects.dsl")),
                Map.of("일반전형", BigDecimal.ONE), sample());
        assertThat(school.valid()).as(school.error()).isTrue();
        assertThat(school.outputs().get("교과")).isEqualByComparingTo("96.6");
        var ged = preview.execute(Files.readString(Path.of("examples/score-policy/ged-subjects.dsl")),
                Map.of("일반전형", BigDecimal.ONE),
                List.of(new ScoreSubject("ged", null, null, "수학", null, new BigDecimal("99"))));
        assertThat(ged.valid()).as(ged.error()).isTrue();
        assertThat(ged.outputs().get("교과")).isEqualByComparingTo("199");
    }
}
