package com.bamdoliro.maru.domain.policy;

import com.bamdoliro.maru.domain.policy.formula.ScoreFormulaPreview;
import java.math.BigDecimal;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreFormulaPreviewTest {
    private final ScoreFormulaPreview preview = new ScoreFormulaPreview();

    @Test
    void 기존과_변경된_기본점수_공식을_표현한다() {
        String source = """
                매개변수 기본점수 = 77.5
                계산 교과 = 기본점수 + 4.8 * (학기21 + 학기22) + 14.4 * 학기31 + 정보 / 2
                출력 점수 = 반올림(교과, 3)
                """;
        var result = preview.execute(source, Map.of("학기21", new BigDecimal("5"),
                "학기22", new BigDecimal("5"), "학기31", new BigDecimal("5"), "정보", new BigDecimal("5")));
        assertThat(result.valid()).isTrue();
        assertThat(result.outputs().get("점수")).isEqualByComparingTo("200");
        assertThat(result.trace()).containsKey("교과");
        var old = preview.execute("출력 점수 = 80 + 4.8 * (5 + 5) + 14.4 * 5", Map.of());
        assertThat(old.outputs().get("점수")).isEqualByComparingTo("200");
    }

    @Test
    void 출결과_봉사_경계조건을_표현한다() {
        String source = """
                계산 환산결석 = 결석 + 내림((지각 + 조퇴 + 결과) / 3)
                출력 출결 = 조건(환산결석 >= 16, 0, 18 - 환산결석)
                출력 봉사 = 조건(시간 < 15, 0, 조건(시간 > 30, 18, 반올림(18 - (30 - 시간) * 0.5, 0)))
                """;
        var result = preview.execute(source, Map.of("결석", new BigDecimal("15"),
                "지각", new BigDecimal("2"), "조퇴", BigDecimal.ONE,
                "결과", BigDecimal.ZERO, "시간", new BigDecimal("15")));
        assertThat(result.valid()).isTrue();
        assertThat(result.outputs().get("출결")).isEqualByComparingTo("0");
        assertThat(result.outputs().get("봉사")).isEqualByComparingTo("11");
    }

    @Test
    void 조건은_선택한_분기만_평가한다() {
        var result = preview.execute("출력 값 = 조건(0, 1 / 0, 7)", Map.of());
        assertThat(result.outputs().get("값")).isEqualByComparingTo("7");
    }

    @Test
    void 소수와_음수_반올림_의미가_고정된다() {
        var result = preview.execute("""
                출력 값 = 0.1 + 0.2
                출력 몫 = 1 / 3
                출력 내림값 = 내림(-1.2)
                출력 절사값 = 절사(-1.2)
                출력 반올림값 = 반올림(-1.25, 1)
                """, Map.of());
        assertThat(result.outputs().get("값")).isEqualByComparingTo("0.3");
        assertThat(result.outputs().get("몫")).isEqualByComparingTo("0.333333333333");
        assertThat(result.outputs().get("내림값")).isEqualByComparingTo("-2");
        assertThat(result.outputs().get("절사값")).isEqualByComparingTo("-1");
        assertThat(result.outputs().get("반올림값")).isEqualByComparingTo("-1.3");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "출력 점수 = 1 / 0", "출력 점수 = 미정의", "출력 점수 = 점수 + 1",
            "출력 점수 = java.lang.System.exit(0)", "출력 점수 = 실행(1)",
            "출력 점수 = 1.2.3", "출력 점수 = 1 +", "출력 점수 = 반올림(1, 1.5)",
            "출력 점수 = 1000000000000 * 1000000000000",
            "출력 점수 = 조건(1, 7, 미정의)", "계산 점수 = 1",
            "출력 점수 = 1\n출력 점수 = 2"
    })
    void 잘못된_식은_부분결과도_반환하지_않는다(String source) {
        var result = preview.execute(source, Map.of());
        assertThat(result.valid()).isFalse();
        assertThat(result.outputs()).isEmpty();
        assertThat(result.trace()).isEmpty();
        assertThat(result.error()).isNotBlank();
    }

    @Test
    void 크기와_중첩_제한을_검증한다() {
        assertThat(preview.execute("출력 값 = " + "(".repeat(100) + "1" + ")".repeat(100), Map.of()).valid()).isFalse();
        assertThat(preview.execute("출력 값 = " + "1+".repeat(100) + "1", Map.of()).valid()).isFalse();
        assertThat(preview.execute("#".repeat(8193), Map.of()).valid()).isFalse();
        assertThat(preview.execute("출력 값 = 입력", Map.of("입력", new BigDecimal("1e1000"))).valid()).isFalse();
    }

    @Test
    void 제공하는_교사_피드백용_예제가_실행된다() throws Exception {
        Map<String, BigDecimal> inputs = new java.util.HashMap<>();
        for (String name : new String[]{"학기21", "학기22", "학기31", "정보"}) {
            inputs.put(name, new BigDecimal("5"));
        }
        for (String name : new String[]{"결석", "지각", "조퇴", "결과"}) {
            inputs.put(name, BigDecimal.ZERO);
        }
        inputs.put("일반전형", BigDecimal.ONE);
        inputs.put("멘토링", BigDecimal.ONE);
        inputs.put("봉사시간", new BigDecimal("30"));
        inputs.put("자격증점수", new BigDecimal("3"));
        var current = preview.execute(Files.readString(Path.of("examples/score-policy/current-school.dsl")), inputs);
        assertThat(current.valid()).as(current.error()).isTrue();
        assertThat(current.outputs().get("일차점수")).isEqualByComparingTo("240");
        var legacy = preview.execute(Files.readString(Path.of("examples/score-policy/legacy-school.dsl")), inputs);
        assertThat(legacy.outputs().get("교과")).isEqualByComparingTo("200");
        String ged = Files.readString(Path.of("examples/score-policy/ged-achievement.dsl"));
        int[] scores = {100, 99, 98, 97, 96, 93, 92, 89, 88, 0};
        int[] levels = {5, 5, 4, 4, 3, 3, 2, 2, 1, 1};
        for (int i = 0; i < scores.length; i++) {
            var result = preview.execute(ged, Map.of("원점수", BigDecimal.valueOf(scores[i])));
            assertThat(result.valid()).as(result.error()).isTrue();
            assertThat(result.outputs().get("성취점수")).isEqualByComparingTo(BigDecimal.valueOf(levels[i]));
        }
    }
}
