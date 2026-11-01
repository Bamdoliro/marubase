package com.bamdoliro.maru.domain.policy.formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bamdoliro.maru.domain.policy.formula.ScoreFormulaPreview.*;

/** 화이트리스트 과목 필드와 유한 목록 연산만 제공한다. */
final class SubjectFunctions {
    static final Set<String> FIELDS = Set.of(
            "학년", "학기", "과목명", "성취", "원성취", "원점수", "점수", "가중치");
    static final Set<String> ROW_FUNCTIONS = Set.of(
            "선택", "성취변경", "점수화", "합계", "평균", "가중평균");
    static final Set<String> FUNCTIONS = Set.of(
            "선택", "성취변경", "점수화", "합계", "평균", "가중평균", "개수");

    private SubjectFunctions() {
    }

    /** 원본은 항상 보존하고 파생 성취/점수만 새 행에 기록한다. */
    public record Row(ScoreSubject original, String achievement, BigDecimal score, BigDecimal weight) {
        Row withAchievement(String value) {
            // 성취를 바꾸면 이전 환산 점수/가중치는 더 이상 유효하지 않다.
            return new Row(original, value, null, null);
        }

        Row withScore(BigDecimal value, BigDecimal newWeight) {
            return new Row(original, achievement, value, newWeight);
        }
    }

    record Rows(List<Row> value) {
        Rows {
            value = List.copyOf(value);
        }
    }

    static Rows input(List<ScoreSubject> subjects) {
        require(subjects != null && subjects.size() <= 240, "과목은 최대 240개입니다.");
        Set<String> ids = new HashSet<>();
        List<Row> rows = new ArrayList<>();
        for (ScoreSubject subject : subjects) {
            require(subject != null, "과목 행은 null일 수 없습니다.");
            require(validText(subject.id()) && validText(subject.name()), "과목 ID/이름은 1~40자여야 합니다.");
            require(ids.add(subject.id()), "과목 ID가 중복됩니다: " + subject.id());
            boolean noSemester = subject.grade() == null && subject.semester() == null;
            boolean validSemester = subject.grade() != null && subject.semester() != null
                    && subject.grade() >= 1 && subject.grade() <= 3
                    && subject.semester() >= 1 && subject.semester() <= 2;
            require(noSemester || validSemester, "학년/학기는 함께 생략하거나 1~3학년, 1~2학기로 입력해주세요.");
            require(subject.achievement() == null || validAchievement(subject.achievement()),
                    "성취도는 A~F 또는 null이어야 합니다.");
            require(subject.achievement() != null || subject.originalScore() != null,
                    "과목에는 성취도 또는 원점수가 필요합니다.");
            if (subject.originalScore() != null) {
                BigDecimal score = checked(subject.originalScore());
                require(score.signum() >= 0 && score.compareTo(BigDecimal.valueOf(100)) <= 0
                        && score.scale() <= 4, "원점수는 0~100, 소수 4자리 이하여야 합니다.");
            }
            rows.add(new Row(subject, subject.achievement(), null, null));
        }
        return new Rows(rows);
    }

    private static boolean validText(String text) {
        return text != null && !text.isBlank() && text.length() <= 40
                && text.chars().noneMatch(Character::isISOControl);
    }

    static boolean validAchievement(String value) {
        return value.length() == 1 && value.charAt(0) >= 'A' && value.charAt(0) <= 'F';
    }

    static Object execute(String name, List<Expression> args, Map<String, Object> values, Budget budget) {
        Object first = args.getFirst().evaluate(values, budget);
        require(first instanceof Rows, name + "의 첫 인자는 과목 목록이어야 합니다.");
        Rows rows = (Rows) first;
        if (name.equals("개수")) {
            return BigDecimal.valueOf(rows.value().size());
        }
        if (Set.of("선택", "성취변경", "점수화").contains(name)) {
            return transform(name, rows, args, values, budget);
        }
        return aggregate(name, rows, args, values, budget);
    }

    private static Rows transform(String name, Rows rows, List<Expression> args,
                                  Map<String, Object> values, Budget budget) {
        List<Row> result = new ArrayList<>();
        for (Row row : rows.value()) {
            budget.operation();
            Map<String, Object> scope = scope(values, row);
            Object value = args.get(1).evaluate(scope, budget);
            switch (name) {
                case "선택" -> {
                    if (truth(number(value))) {
                        result.add(row);
                    }
                }
                case "성취변경" -> {
                    require(value instanceof String && validAchievement((String) value),
                            "성취변경 결과는 A~F여야 합니다.");
                    result.add(row.withAchievement((String) value));
                }
                case "점수화" -> {
                    BigDecimal score = number(value);
                    BigDecimal weight = number(args.get(2).evaluate(scope, budget));
                    require(weight.signum() >= 0, "가중치는 음수일 수 없습니다.");
                    result.add(row.withScore(score, weight));
                }
                default -> throw new InvalidFormula("지원하지 않는 과목 변환입니다.");
            }
        }
        return new Rows(result);
    }

    private static BigDecimal aggregate(String name, Rows rows, List<Expression> args,
                                        Map<String, Object> values, Budget budget) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal denominator = BigDecimal.ZERO;
        for (Row row : rows.value()) {
            budget.operation();
            Map<String, Object> scope = scope(values, row);
            BigDecimal score = number(args.get(1).evaluate(scope, budget));
            BigDecimal weight = name.equals("가중평균")
                    ? number(args.get(2).evaluate(scope, budget)) : BigDecimal.ONE;
            require(weight.signum() >= 0, "가중치는 음수일 수 없습니다.");
            // 합계/가중 합산 중에는 반올림하지 않고 마지막 나눗셈에서만 소수 12자리로 반올림한다.
            BigDecimal term = score.multiply(weight);
            require(term.abs().compareTo(MAX_VALUE) <= 0, "집계 중 숫자 범위를 초과했습니다.");
            total = total.add(term);
            require(total.abs().compareTo(MAX_VALUE) <= 0, "집계 중 숫자 범위를 초과했습니다.");
            denominator = checked(denominator.add(weight));
        }
        if (name.equals("합계")) {
            return checked(total.setScale(12, RoundingMode.HALF_UP).stripTrailingZeros());
        }
        require(denominator.signum() > 0, "빈 목록 또는 가중치 합계 0의 평균은 정의되지 않습니다. 조건으로 기본값을 명시해주세요.");
        return checked(total.divide(denominator, 12, RoundingMode.HALF_UP).stripTrailingZeros());
    }

    private static Map<String, Object> scope(Map<String, Object> values, Row row) {
        Map<String, Object> scope = new LinkedHashMap<>(values);
        ScoreSubject original = row.original();
        scope.put("학년", original.grade() == null ? Missing.VALUE : BigDecimal.valueOf(original.grade()));
        scope.put("학기", original.semester() == null ? Missing.VALUE : BigDecimal.valueOf(original.semester()));
        scope.put("과목명", original.name());
        scope.put("성취", present(row.achievement()));
        scope.put("원성취", present(original.achievement()));
        scope.put("원점수", present(original.originalScore()));
        scope.put("점수", present(row.score()));
        scope.put("가중치", present(row.weight()));
        return scope;
    }

    private static Object present(Object value) {
        return value == null ? Missing.VALUE : value;
    }
}
