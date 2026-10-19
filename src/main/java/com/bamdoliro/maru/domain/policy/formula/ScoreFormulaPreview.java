package com.bamdoliro.maru.domain.policy.formula;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 미리보기 전용 순수 계산기. 외부 런타임/eval, 객체 접근, 반복문, DB 접근을 지원하지 않는다.
 * 각 요청마다 파서와 평가 예산을 생성하므로 공유 가변 상태가 없다.
 */
@Component
public class ScoreFormulaPreview {
    private static final int MAX_SOURCE_LENGTH = 8192;
    private static final int MAX_STATEMENTS = 64;
    private static final int MAX_TOKENS = 2048;
    private static final int MAX_DEPTH = 32;
    private static final int MAX_OPERATIONS = 4096;
    private static final BigDecimal MAX_VALUE = new BigDecimal("1000000000000");

    public Result execute(String source, Map<String, BigDecimal> inputs) {
        try {
            return evaluate(source, inputs);
        } catch (InvalidFormula exception) {
            return new Result(false, Map.of(), Map.of(), exception.getMessage());
        }
    }

    private Result evaluate(String source, Map<String, BigDecimal> inputs) {
        require(source != null && !source.isBlank() && source.length() <= MAX_SOURCE_LENGTH,
                "수식은 1~8192자여야 합니다.");
        require(inputs != null && inputs.size() <= 40, "입력은 최대 40개입니다.");
        Map<String, BigDecimal> values = new LinkedHashMap<>();
        inputs.forEach((name, value) -> {
            require(identifier(name) && name.length() <= 40, "입력 이름이 올바르지 않습니다.");
            values.put(name, checked(value));
        });
        Map<String, BigDecimal> trace = new LinkedHashMap<>();
        Map<String, BigDecimal> outputs = new LinkedHashMap<>();
        Budget budget = new Budget();
        int statementCount = 0;
        for (String line : source.split("\\R")) {
            String statement = line.strip();
            if (statement.isEmpty() || statement.startsWith("#")) {
                continue;
            }
            require(++statementCount <= MAX_STATEMENTS, "정의는 최대 64개입니다.");
            int space = statement.indexOf(' ');
            int equal = statement.indexOf('=');
            require(space > 0 && equal > space, "형식: 매개변수/계산/출력 이름 = 식");
            String kind = statement.substring(0, space);
            require(Set.of("매개변수", "계산", "출력").contains(kind), "알 수 없는 정의 유형입니다.");
            String name = statement.substring(space + 1, equal).strip();
            require(identifier(name) && name.length() <= 40, "정의 이름이 올바르지 않습니다.");
            require(!values.containsKey(name), "입력/정의 이름이 중복됩니다: " + name);
            Parser parser = new Parser(statement.substring(equal + 1), values.keySet(), budget);
            Expression expression = parser.parse();
            BigDecimal result = checked(expression.evaluate(values, budget));
            values.put(name, result);
            trace.put(name, result);
            if (kind.equals("출력")) {
                outputs.put(name, result);
            }
        }
        require(!outputs.isEmpty(), "출력을 하나 이상 정의해주세요.");
        return new Result(true, outputs, trace, null);
    }

    public record Result(boolean valid, Map<String, BigDecimal> outputs,
                         Map<String, BigDecimal> trace, String error) {
    }

    private static boolean identifier(String value) {
        if (value == null || value.isEmpty()
                || !(Character.isLetter(value.charAt(0)) || value.charAt(0) == '_')) {
            return false;
        }
        return value.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_');
    }

    private static BigDecimal checked(BigDecimal value) {
        require(value != null && value.precision() <= 38 && value.scale() >= -12
                        && value.scale() <= 12 && value.abs().compareTo(MAX_VALUE) <= 0,
                "숫자는 절댓값 10^12 이하, 소수 12자리 이하 범위여야 합니다.");
        return value;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new InvalidFormula(message);
        }
    }

    private static BigDecimal bool(boolean value) {
        return value ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    private static boolean truth(BigDecimal value) {
        return value.signum() != 0;
    }

    private static final class InvalidFormula extends RuntimeException {
        private InvalidFormula(String message) {
            super(message);
        }
    }

    private static final class Budget {
        private int tokens;
        private int operations;

        void token() {
            require(++tokens <= MAX_TOKENS, "토큰 한도를 초과했습니다.");
        }

        void operation() {
            require(++operations <= MAX_OPERATIONS, "계산 한도를 초과했습니다.");
        }
    }

    @FunctionalInterface
    private interface Expression {
        BigDecimal evaluate(Map<String, BigDecimal> values, Budget budget);

        default int depth() {
            return 1;
        }
    }

    private record Literal(BigDecimal value) implements Expression {
        public BigDecimal evaluate(Map<String, BigDecimal> values, Budget budget) {
            budget.operation();
            return value;
        }
    }

    private record Variable(String name) implements Expression {
        public BigDecimal evaluate(Map<String, BigDecimal> values, Budget budget) {
            budget.operation();
            return values.get(name);
        }
    }

    private record Unary(String operator, Expression operand) implements Expression {
        public int depth() {
            return 1 + operand.depth();
        }

        public BigDecimal evaluate(Map<String, BigDecimal> values, Budget budget) {
            budget.operation();
            BigDecimal value = operand.evaluate(values, budget);
            return switch (operator) {
                case "-" -> value.negate();
                case "!" -> bool(!truth(value));
                default -> value;
            };
        }
    }

    private record Binary(String operator, Expression left, Expression right) implements Expression {
        public int depth() {
            return 1 + Math.max(left.depth(), right.depth());
        }

        public BigDecimal evaluate(Map<String, BigDecimal> values, Budget budget) {
            budget.operation();
            BigDecimal a = left.evaluate(values, budget);
            if (operator.equals("&&") && !truth(a)) {
                return BigDecimal.ZERO;
            }
            if (operator.equals("||") && truth(a)) {
                return BigDecimal.ONE;
            }
            BigDecimal b = right.evaluate(values, budget);
            return checked(switch (operator) {
                case "+" -> a.add(b);
                case "-" -> a.subtract(b);
                case "*" -> a.multiply(b).setScale(12, RoundingMode.HALF_UP).stripTrailingZeros();
                case "/" -> {
                    require(b.signum() != 0, "0으로 나눌 수 없습니다.");
                    yield a.divide(b, 12, RoundingMode.HALF_UP).stripTrailingZeros();
                }
                case "<" -> bool(a.compareTo(b) < 0);
                case "<=" -> bool(a.compareTo(b) <= 0);
                case ">" -> bool(a.compareTo(b) > 0);
                case ">=" -> bool(a.compareTo(b) >= 0);
                case "==" -> bool(a.compareTo(b) == 0);
                case "!=" -> bool(a.compareTo(b) != 0);
                case "&&", "||" -> bool(truth(b));
                default -> throw new InvalidFormula("지원하지 않는 연산자입니다.");
            });
        }
    }

    private record Function(String name, List<Expression> arguments) implements Expression {
        public int depth() {
            return 1 + arguments.stream().mapToInt(Expression::depth).max().orElse(0);
        }

        public BigDecimal evaluate(Map<String, BigDecimal> values, Budget budget) {
            budget.operation();
            BigDecimal first = arguments.getFirst().evaluate(values, budget);
            if (name.equals("조건")) {
                return arguments.get(truth(first) ? 1 : 2).evaluate(values, budget);
            }
            List<BigDecimal> args = new ArrayList<>();
            args.add(first);
            for (int i = 1; i < arguments.size(); i++) {
                args.add(arguments.get(i).evaluate(values, budget));
            }
            return checked(switch (name) {
                case "최소" -> first.min(args.get(1));
                case "최대" -> first.max(args.get(1));
                case "내림" -> first.setScale(0, RoundingMode.FLOOR);
                case "올림" -> first.setScale(0, RoundingMode.CEILING);
                case "절사" -> first.setScale(0, RoundingMode.DOWN);
                case "절댓값" -> first.abs();
                case "반올림" -> {
                    BigDecimal places = args.get(1).stripTrailingZeros();
                    require(places.scale() <= 0 && places.signum() >= 0
                            && places.compareTo(BigDecimal.valueOf(12)) <= 0,
                            "반올림 자릿수는 0~12 정수여야 합니다.");
                    yield first.setScale(places.intValueExact(), RoundingMode.HALF_UP);
                }
                default -> throw new InvalidFormula("지원하지 않는 함수입니다.");
            });
        }
    }

    private static final class Parser {
        private final List<String> tokens = new ArrayList<>();
        private final Set<String> names;
        private int cursor;

        Parser(String source, Set<String> names, Budget budget) {
            this.names = Set.copyOf(names);
            tokenize(source, budget);
        }

        Expression parse() {
            Expression value = binary(0, 0);
            checkDepth(value.depth());
            require(cursor == tokens.size(), "식 끝에 처리하지 못한 문자가 있습니다.");
            return value;
        }

        private void tokenize(String source, Budget budget) {
            for (int i = 0; i < source.length();) {
                char ch = source.charAt(i);
                if (Character.isWhitespace(ch)) {
                    i++;
                    continue;
                }
                int start = i++;
                if (Character.isLetter(ch) || ch == '_') {
                    while (i < source.length() && (Character.isLetterOrDigit(source.charAt(i))
                            || source.charAt(i) == '_')) {
                        i++;
                    }
                } else if (ch >= '0' && ch <= '9') {
                    while (i < source.length() && ((source.charAt(i) >= '0'
                            && source.charAt(i) <= '9') || source.charAt(i) == '.')) {
                        i++;
                    }
                } else {
                    require("+-*/(),!<>=&|".indexOf(ch) >= 0, "허용되지 않는 문자가 있습니다.");
                    if (i < source.length()
                            && Set.of("<=", ">=", "==", "!=", "&&", "||")
                            .contains(source.substring(start, i + 1))) {
                        i++;
                    }
                }
                budget.token();
                tokens.add(source.substring(start, i));
            }
        }

        private Expression binary(int minimum, int depth) {
            checkDepth(depth);
            Expression left = primary(depth + 1);
            while (cursor < tokens.size()) {
                String operator = tokens.get(cursor);
                int precedence = precedence(operator);
                if (precedence < minimum) {
                    break;
                }
                cursor++;
                Expression right = binary(precedence + 1, depth + 1);
                left = new Binary(operator, left, right);
                checkDepth(left.depth());
                // 왼쪽으로 긴 덧셈 연쇄도 평가 시 깊이 제한을 우회하지 않게 한다.
                depth++;
                checkDepth(depth);
            }
            return left;
        }

        private Expression primary(int depth) {
            checkDepth(depth);
            require(cursor < tokens.size(), "식이 완성되지 않았습니다.");
            String token = tokens.get(cursor++);
            if (Set.of("+", "-", "!").contains(token)) {
                return new Unary(token, primary(depth + 1));
            }
            if (token.equals("(")) {
                Expression expression = binary(0, depth + 1);
                expect(")");
                return expression;
            }
            if (token.charAt(0) >= '0' && token.charAt(0) <= '9') {
                try {
                    return new Literal(checked(new BigDecimal(token, MathContext.UNLIMITED)));
                } catch (NumberFormatException exception) {
                    throw new InvalidFormula("숫자 형식이 올바르지 않습니다.");
                }
            }
            require(identifier(token), "식 또는 변수 이름이 필요합니다.");
            if (take("(")) {
                int count = switch (token) {
                    case "조건" -> 3;
                    case "최소", "최대", "반올림" -> 2;
                    case "내림", "올림", "절사", "절댓값" -> 1;
                    default -> throw new InvalidFormula("지원하지 않는 함수입니다: " + token);
                };
                List<Expression> arguments = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    if (i > 0) {
                        expect(",");
                    }
                    arguments.add(binary(0, depth + 1));
                }
                expect(")");
                return new Function(token, arguments);
            }
            require(names.contains(token), "앞서 정의되지 않은 입력/변수입니다: " + token);
            return new Variable(token);
        }

        private int precedence(String operator) {
            return switch (operator) {
                case "||" -> 0;
                case "&&" -> 1;
                case "==", "!=" -> 2;
                case "<", "<=", ">", ">=" -> 3;
                case "+", "-" -> 4;
                case "*", "/" -> 5;
                default -> -1;
            };
        }

        private boolean take(String token) {
            if (cursor < tokens.size() && tokens.get(cursor).equals(token)) {
                cursor++;
                return true;
            }
            return false;
        }

        private void expect(String token) {
            require(take(token), "필요한 구분자: " + token);
        }

        private void checkDepth(int depth) {
            require(depth <= MAX_DEPTH, "식 중첩/연산 연쇄는 최대 32단계입니다.");
        }
    }
}
