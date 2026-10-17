package com.bamdoliro.maru.shared.ratelimit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @Mock
    private RateLimitService rateLimitService;

    @Test
    void 요청_객체에서_식별자를_읽어_요청_횟수를_확인한다() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestController());
        factory.addAspect(new RateLimitAspect(rateLimitService));
        TestController proxy = factory.getProxy();

        proxy.execute(new TestRequest("01012345678"));

        verify(rateLimitService, times(1)).consume(
                RateLimitPolicy.LOGIN_ACCOUNT,
                "01012345678"
        );
    }

    static class TestController {

        @RateLimited(policy = RateLimitPolicy.LOGIN_ACCOUNT, key = "#p0.phoneNumber")
        public void execute(TestRequest request) {
        }
    }

    @Getter
    @AllArgsConstructor
    static class TestRequest {

        private String phoneNumber;
    }
}
