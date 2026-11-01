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
    private static final int MAX_OPERATIONS = 50000;
    static final BigDecimal MAX_VALUE = new BigDecimal("1000000000000");

    public Result execute(String source, Map<String, BigDecimal> inputs) {
        return execute(source, inputs, List.of());
    }

    public Result execute(String source, Map<String, BigDecimal> inputs, List<ScoreSubject> subjects) {
        try {
            return evaluate(source, inputs, subjects);
        } catch (InvalidFormula exception) {
            return new Result(false, Map.of(), Map.of(), exception.getMessage());
        }
    }

    private Result evaluate(String source, Map<String, BigDecimal> inputs, List<ScoreSubject> subjects) {
        require(source != null && !source.isBlank() && source.length() <= MAX_SOURCE_LENGTH,
                "수식은 1~8192자여야 합니다.");
        require(inputs != null && inputs.size() <= 40, "입력은 최대 40개입니다.");
        Map<String, Object> values = new LinkedHashMap<>();
        inputs.forEach((name, value) -> {
            validateName(name);
            values.put(name, checked(value));
        });
        values.put("과목들", SubjectFunctions.input(subjects));
        Map<String, Object> trace = new LinkedHashMap<>();
        Map<String, BigDecimal> outputs = new LinkedHashMap<>();
        Budget budget = new Budget();
        for (String statement : statements(source)) {
            int space = statement.indexOf(' ');
            int equal = statement.indexOf('=');
            require(space > 0 && equal > space, "형식: 매개변수/계산/출력 이름 = 식");
            String kind = statement.substring(0, space);
            require(Set.of("매개변수", "계산", "출력").contains(kind), "알 수 없는 정의 유형입니다.");
            String name = statement.substring(space + 1, equal).strip();
            validateName(name);
            require(!values.containsKey(name), "입력/정의 이름이 중복됩니다: " + name);
            Parser parser = new Parser(statement.substring(equal + 1), values.keySet(), budget);
            Expression expression = parser.parse();
            Map<String, ValueType> types = new LinkedHashMap<>();
            values.forEach((key, value) -> types.put(key, typeOf(value)));
            validateTypes(expression, types);
            Object result = expression.evaluate(values, budget);
            values.put(name, result);
            if (result instanceof SubjectFunctions.Rows rows) {
                budget.traceRows(rows.value().size());
                trace.put(name, rows.value());
            } else {
                require(result != Missing.VALUE, "누락된 값을 정의할 수 없습니다: " + name);
                trace.put(name, result);
            }
            if (kind.equals("출력")) {
                outputs.put(name, number(result));
            }
        }
        require(!outputs.isEmpty(), "출력을 하나 이상 정의해주세요.");
        return new Result(true, outputs, trace, null);
    }

    public record Result(boolean valid, Map<String, BigDecimal> outputs,
                         Map<String, Object> trace, String error) {
    }

    enum Missing { VALUE }

    private static void validateName(String name) {
        require(identifier(name) && name.length() <= 40, "이름은 문자로 시작하는 1~40자여야 합니다.");
        require(!Set.of("과목들", "없음").contains(name),
                "예약된 이름은 정의하거나 입력할 수 없습니다: " + name);
    }

    /** 괄호 안 줄바꿈과 문자열 밖 주석을 지원한다. 임의 문장/블록 실행은 없다. */
    private static List<String> statements(String source) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        boolean escaped = false;
        boolean comment = false;
        int depth = 0;
        for (char ch : source.toCharArray()) {
            if (comment && ch != '\n' && ch != '\r') {
                continue;
            }
            if (quoted) {
                require(ch != '\n' && ch != '\r', "문자열 안의 줄바꿈은 허용하지 않습니다.");
                current.append(ch);
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    quoted = false;
                }
            } else if (ch == '#') {
                comment = true;
            } else if (ch == '\n' || ch == '\r') {
                comment = false;
                if (depth == 0) {
                    addStatement(result, current);
                } else {
                    current.append(' ');
                }
            } else {
                current.append(ch);
                if (ch == '"') {
                    quoted = true;
                } else if (ch == '(') {
                    require(++depth <= MAX_DEPTH, "괄호 중첩 한도를 초과했습니다.");
                } else if (ch == ')') {
                    require(--depth >= 0, "닫는 괄호가 너무 많습니다.");
                }
            }
        }
        require(!quoted && depth == 0, "문자열 또는 괄호가 닫히지 않았습니다.");
        addStatement(result, current);
        return result;
    }

    private static void addStatement(List<String> result, StringBuilder current) {
        String statement = current.toString().strip();
        if (!statement.isEmpty()) {
            require(result.size() < MAX_STATEMENTS, "정의는 최대 64개입니다.");
            result.add(statement);
        }
        current.setLength(0);
    }

    private static boolean identifier(String value) {
        if (value == null || value.isEmpty()
                || !(Character.isLetter(value.charAt(0)) || value.charAt(0) == '_')) {
            return false;
        }
        return value.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_');
    }

    static BigDecimal checked(BigDecimal value) {
        require(value != null && value.precision() <= 38 && value.scale() >= -12
                        && value.scale() <= 12 && value.abs().compareTo(MAX_VALUE) <= 0,
                "숫자는 절댓값 10^12 이하, 소수 12자리 이하 범위여야 합니다.");
        return value;
    }

    static void require(boolean condition, String message) {
        if (!condition) {
            throw new InvalidFormula(message);
        }
    }

    private static BigDecimal bool(boolean value) {
        return value ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    static boolean truth(BigDecimal value) {
        return value.signum() != 0;
    }

    static final class InvalidFormula extends RuntimeException {
        InvalidFormula(String message) {
            super(message);
        }
    }

    static final class Budget {
        private int tokens;
        private int operations;
        private int traceRows;

        void traceRows(int count) {
            traceRows += count;
            require(traceRows <= 2048, "계산 과정의 과목 행은 총 2048개까지 반환합니다.");
        }

        void token() {
            require(++tokens <= MAX_TOKENS, "토큰 한도를 초과했습니다.");
        }

        void operation() {
            require(++operations <= MAX_OPERATIONS, "계산 한도를 초과했습니다.");
        }
    }

    @FunctionalInterface
    interface Expression {
        Object evaluate(Map<String, Object> values, Budget budget);

        default int depth() {
            return 1;
        }
    }

    private record Literal(Object value) implements Expression {
        public Object evaluate(Map<String, Object> values, Budget budget) {
            budget.operation();
            return value;
        }
    }

    private record Variable(String name) implements Expression {
        public Object evaluate(Map<String, Object> values, Budget budget) {
            budget.operation();
            return values.get(name);
        }
    }

    private record Unary(String operator, Expression operand) implements Expression {
        public int depth() {
            return 1 + operand.depth();
        }

        public Object evaluate(Map<String, Object> values, Budget budget) {
            budget.operation();
            BigDecimal value = number(operand.evaluate(values, budget));
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

        public Object evaluate(Map<String, Object> values, Budget budget) {
            budget.operation();
            Object a = left.evaluate(values, budget);
            if (operator.equals("&&") && !truth(number(a))) {
                return BigDecimal.ZERO;
            }
            if (operator.equals("||") && truth(number(a))) {
                return BigDecimal.ONE;
            }
            Object b = right.evaluate(values, budget);
            if (Set.of("==", "!=").contains(operator)) {
                boolean equal = equivalent(a, b);
                return bool(operator.equals("==") ? equal : !equal);
            }
            BigDecimal x = number(a);
            BigDecimal y = number(b);
            return checked(switch (operator) {
                case "+" -> x.add(y);
                case "-" -> x.subtract(y);
                case "*" -> x.multiply(y).setScale(12, RoundingMode.HALF_UP).stripTrailingZeros();
                case "/" -> {
                    require(y.signum() != 0, "0으로 나눌 수 없습니다.");
                    yield x.divide(y, 12, RoundingMode.HALF_UP).stripTrailingZeros();
                }
                case "<" -> bool(x.compareTo(y) < 0);
                case "<=" -> bool(x.compareTo(y) <= 0);
                case ">" -> bool(x.compareTo(y) > 0);
                case ">=" -> bool(x.compareTo(y) >= 0);
                case "&&", "||" -> bool(truth(y));
                default -> throw new InvalidFormula("지원하지 않는 연산자입니다.");
            });
        }
    }

    private enum ValueType { NUMBER, TEXT, ROWS, MISSING }

    private static ValueType typeOf(Object value) {
        if (value instanceof BigDecimal) {
            return ValueType.NUMBER;
        }
        if (value instanceof String) {
            return ValueType.TEXT;
        }
        return value instanceof SubjectFunctions.Rows ? ValueType.ROWS : ValueType.MISSING;
    }

    /** 빈 과목 목록이나 선택되지 않은 분기에서도 잘못된 필드/인자 타입을 거부한다. */
    private static ValueType validateTypes(Expression expression, Map<String, ValueType> scope) {
        if (expression instanceof Literal literal) {
            return typeOf(literal.value());
        }
        if (expression instanceof Variable variable) {
            ValueType type = scope.get(variable.name());
            require(type != null, "변수 범위가 올바르지 않습니다: " + variable.name());
            return type;
        }
        if (expression instanceof Unary unary) {
            expectType(validateTypes(unary.operand(), scope), ValueType.NUMBER);
            return ValueType.NUMBER;
        }
        if (expression instanceof Binary binary) {
            ValueType left = validateTypes(binary.left(), scope);
            ValueType right = validateTypes(binary.right(), scope);
            if (Set.of("==", "!=").contains(binary.operator())) {
                require(left != ValueType.ROWS && right != ValueType.ROWS, "목록은 직접 비교할 수 없습니다.");
                require(left == right || left == ValueType.MISSING || right == ValueType.MISSING,
                        "비교하는 값의 타입이 다릅니다.");
            } else {
                expectType(left, ValueType.NUMBER);
                expectType(right, ValueType.NUMBER);
            }
            return ValueType.NUMBER;
        }
        Function function = (Function) expression;
        List<ValueType> args = new ArrayList<>();
        for (int i = 0; i < function.arguments().size(); i++) {
            Map<String, ValueType> local = new LinkedHashMap<>(scope);
            if (i > 0 && SubjectFunctions.ROW_FUNCTIONS.contains(function.name())) {
                for (String field : SubjectFunctions.FIELDS) {
                    local.put(field, Set.of("과목명", "성취", "원성취").contains(field)
                            ? ValueType.TEXT : ValueType.NUMBER);
                }
            }
            args.add(validateTypes(function.arguments().get(i), local));
        }
        return functionType(function.name(), args);
    }

    private static ValueType functionType(String name, List<ValueType> args) {
        ValueType first = args.getFirst();
        if (SubjectFunctions.FUNCTIONS.contains(name)) {
            expectType(first, ValueType.ROWS);
            if (name.equals("성취변경")) {
                expectType(args.get(1), ValueType.TEXT);
            } else {
                for (int i = 1; i < args.size(); i++) {
                    expectType(args.get(i), ValueType.NUMBER);
                }
            }
            return Set.of("선택", "성취변경", "점수화").contains(name) ? ValueType.ROWS : ValueType.NUMBER;
        }
        if (name.equals("조건")) {
            expectType(first, ValueType.NUMBER);
            require(args.get(1) == args.get(2), "조건의 두 결과는 같은 타입이어야 합니다.");
            return args.get(1);
        }
        if (name.equals("있음")) {
            return ValueType.NUMBER;
        }
        if (name.equals("포함")) {
            scalarType(first);
            args.forEach(type -> expectType(type, first));
            return ValueType.NUMBER;
        }
        if (name.equals("대응")) {
            scalarType(first);
            ValueType result = args.get(2);
            scalarType(result);
            for (int i = 1; i < args.size(); i += 2) {
                expectType(args.get(i), first);
                expectType(args.get(i + 1), result);
            }
            return result;
        }
        if (name.equals("구간")) {
            expectType(first, ValueType.NUMBER);
            ValueType result = args.getLast();
            scalarType(result);
            for (int i = 1; i < args.size() - 1; i += 2) {
                expectType(args.get(i), ValueType.NUMBER);
                expectType(args.get(i + 1), result);
            }
            return result;
        }
        args.forEach(type -> expectType(type, ValueType.NUMBER));
        return ValueType.NUMBER;
    }

    private static void expectType(ValueType actual, ValueType expected) {
        require(actual == expected, "식/함수 인자 타입이 다릅니다. 필요: " + expected + ", 실제: " + actual);
    }

    private static void scalarType(ValueType type) {
        require(type == ValueType.NUMBER || type == ValueType.TEXT, "환산/포함에는 숫자 또는 문자열이 필요합니다.");
    }

    static BigDecimal number(Object value) {
        require(value instanceof BigDecimal, "숫자가 필요합니다. 누락/문자열/목록을 0으로 취급하지 않습니다.");
        return checked((BigDecimal) value);
    }

    private static boolean equivalent(Object left, Object right) {
        if (left == Missing.VALUE || right == Missing.VALUE) {
            return left == right;
        }
        if (left instanceof String a && right instanceof String b) {
            return a.equals(b);
        }
        return number(left).compareTo(number(right)) == 0;
    }

    private record Function(String name, List<Expression> arguments) implements Expression {
        public int depth() {
            return 1 + arguments.stream().mapToInt(Expression::depth).max().orElse(0);
        }

        public Object evaluate(Map<String, Object> values, Budget budget) {
            budget.operation();
            if (SubjectFunctions.FUNCTIONS.contains(name)) {
                return SubjectFunctions.execute(name, arguments, values, budget);
            }
            Object first = arguments.getFirst().evaluate(values, budget);
            if (name.equals("조건")) {
                return arguments.get(truth(number(first)) ? 1 : 2).evaluate(values, budget);
            }
            if (name.equals("있음")) {
                return bool(first != Missing.VALUE);
            }
            List<Object> args = new ArrayList<>();
            args.add(first);
            for (int i = 1; i < arguments.size(); i++) {
                args.add(arguments.get(i).evaluate(values, budget));
            }
            if (name.equals("포함")) {
                boolean found = false;
                for (int i = 1; i < args.size(); i++) {
                    budget.operation();
                    found |= equivalent(first, args.get(i));
                }
                return bool(found);
            }
            if (name.equals("대응")) {
                return mapping(args, budget);
            }
            if (name.equals("구간")) {
                return interval(args, budget);
            }
            BigDecimal value = number(first);
            return checked(switch (name) {
                case "최소" -> value.min(number(args.get(1)));
                case "최대" -> value.max(number(args.get(1)));
                case "내림" -> value.setScale(0, RoundingMode.FLOOR);
                case "올림" -> value.setScale(0, RoundingMode.CEILING);
                case "절사" -> value.setScale(0, RoundingMode.DOWN);
                case "절댓값" -> value.abs();
                case "반올림" -> {
                    BigDecimal places = number(args.get(1)).stripTrailingZeros();
                    require(places.scale() <= 0 && places.signum() >= 0
                            && places.compareTo(BigDecimal.valueOf(12)) <= 0,
                            "반올림 자릿수는 0~12 정수여야 합니다.");
                    yield value.setScale(places.intValueExact(), RoundingMode.HALF_UP);
                }
                default -> throw new InvalidFormula("지원하지 않는 함수입니다.");
            });
        }

        private Object mapping(List<Object> args, Budget budget) {
            Object input = scalar(args.getFirst());
            Object result = Missing.VALUE;
            for (int i = 1; i < args.size(); i += 2) {
                Object key = scalar(args.get(i));
                Object value = scalar(args.get(i + 1));
                for (int j = 1; j < i; j += 2) {
                    budget.operation();
                    require(!equivalent(key, args.get(j)), "대응의 키가 중복됩니다.");
                }
                if (equivalent(input, key)) {
                    result = value;
                }
            }
            require(result != Missing.VALUE, "대응에 없는 값입니다. 모든 성취도/F 처리를 명시해주세요.");
            return result;
        }

        private Object interval(List<Object> args, Budget budget) {
            BigDecimal input = number(args.getFirst());
            BigDecimal previous = null;
            Object result = Missing.VALUE;
            for (int i = 1; i < args.size() - 1; i += 2) {
                budget.operation();
                BigDecimal threshold = number(args.get(i));
                require(previous == null || previous.compareTo(threshold) > 0,
                        "구간 하한은 중복 없이 큰 값부터 적어주세요.");
                Object value = scalar(args.get(i + 1));
                if (result == Missing.VALUE && input.compareTo(threshold) >= 0) {
                    result = value;
                }
                previous = threshold;
            }
            Object fallback = scalar(args.getLast());
            return result == Missing.VALUE ? fallback : result;
        }

        private Object scalar(Object value) {
            require(value instanceof String || value instanceof BigDecimal, "환산에는 숫자 또는 문자열만 사용할 수 있습니다.");
            return value;
        }
    }

    private static final class Parser {
        private final List<String> tokens = new ArrayList<>();
        private final Set<String> names;
        private int cursor;
        private int rowScope;

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
                if (ch == '"') {
                    boolean closed = false;
                    while (i < source.length()) {
                        char next = source.charAt(i++);
                        if (next == '\\') {
                            require(i < source.length() && (source.charAt(i) == '"' || source.charAt(i) == '\\'),
                                    "문자열은 따옴표/역슬래시 이스케이프만 지원합니다.");
                            i++;
                        } else if (next == '"') {
                            closed = true;
                            break;
                        } else {
                            require(!Character.isISOControl(next), "문자열 제어문자는 허용하지 않습니다.");
                        }
                    }
                    require(closed && i - start <= 82, "문자열은 닫힌 따옴표 안에 최대 80자로 적어주세요.");
                } else if (Character.isLetter(ch) || ch == '_') {
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
            if (token.startsWith("\"")) {
                String value = token.substring(1, token.length() - 1)
                        .replace("\\\"", "\"").replace("\\\\", "\\");
                return new Literal(value);
            }
            if (token.equals("없음")) {
                return new Literal(Missing.VALUE);
            }
            require(identifier(token), "식 또는 변수 이름이 필요합니다.");
            if (take("(")) {
                require(functionKnown(token), "지원하지 않는 함수입니다: " + token);
                List<Expression> arguments = new ArrayList<>();
                if (!take(")")) {
                    do {
                        require(arguments.size() < 32, "함수 인자는 최대 32개입니다.");
                        boolean rowArgument = SubjectFunctions.ROW_FUNCTIONS.contains(token) && !arguments.isEmpty();
                        if (rowArgument) {
                            rowScope++;
                        }
                        arguments.add(binary(0, depth + 1));
                        if (rowArgument) {
                            rowScope--;
                        }
                    } while (take(","));
                    expect(")");
                }
                validateArity(token, arguments.size());
                return new Function(token, arguments);
            }
            require(names.contains(token) || (rowScope > 0 && SubjectFunctions.FIELDS.contains(token)),
                    "앞서 정의되지 않은 입력/변수입니다: " + token);
            return new Variable(token);
        }

        private boolean functionKnown(String name) {
            return SubjectFunctions.FUNCTIONS.contains(name)
                    || Set.of("조건", "최소", "최대", "반올림", "내림", "올림", "절사", "절댓값",
                    "있음", "포함", "대응", "구간").contains(name);
        }

        private void validateArity(String name, int count) {
            boolean valid = switch (name) {
                case "조건", "점수화", "가중평균" -> count == 3;
                case "최소", "최대", "반올림", "선택", "성취변경", "합계", "평균" -> count == 2;
                case "내림", "올림", "절사", "절댓값", "있음", "개수" -> count == 1;
                case "포함" -> count >= 2;
                case "대응" -> count >= 3 && count % 2 == 1;
                case "구간" -> count >= 4 && count % 2 == 0;
                default -> false;
            };
            require(valid, "함수 인자 수가 올바르지 않습니다: " + name);
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
