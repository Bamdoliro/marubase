package com.bamdoliro.maru.shared.ratelimit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    private final RateLimitService rateLimitService;

    @Before("@annotation(rateLimited)")
    public void rateLimit(JoinPoint joinPoint, RateLimited rateLimited) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] arguments = joinPoint.getArgs();
        for (int index = 0; index < arguments.length; index++) {
            context.setVariable("p" + index, arguments[index]);
        }

        String identifier = EXPRESSION_PARSER
                .parseExpression(rateLimited.key())
                .getValue(context, String.class);
        if (identifier == null || identifier.isBlank()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            throw new IllegalStateException(signature.getMethod().getName() + " 요청 제한 키가 비어 있습니다.");
        }

        rateLimitService.consume(rateLimited.policy(), identifier);
    }
}
