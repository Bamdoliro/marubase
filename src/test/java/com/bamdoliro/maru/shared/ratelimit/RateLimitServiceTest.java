package com.bamdoliro.maru.shared.ratelimit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @InjectMocks
    private RateLimitService rateLimitService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Test
    void 제한_횟수_이내의_요청은_허용한다() {
        doReturn(3L).when(redisTemplate).execute(any(RedisScript.class), anyList(), any());

        assertDoesNotThrow(() -> rateLimitService.consume(
                RateLimitPolicy.LOGIN_ACCOUNT,
                "01012345678"
        ));
    }

    @Test
    void 제한_횟수를_초과하면_요청을_거부한다() {
        doReturn(11L).when(redisTemplate).execute(any(RedisScript.class), anyList(), any());

        assertThrows(
                RateLimitExceededException.class,
                () -> rateLimitService.consume(RateLimitPolicy.LOGIN_ACCOUNT, "01012345678")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void Redis_키에_전화번호를_그대로_저장하지_않는다() {
        doReturn(1L).when(redisTemplate).execute(any(RedisScript.class), anyList(), any());
        ArgumentCaptor<List<String>> keyCaptor = ArgumentCaptor.forClass(List.class);

        rateLimitService.consume(RateLimitPolicy.LOGIN_ACCOUNT, "01012345678");

        verify(redisTemplate).execute(any(RedisScript.class), keyCaptor.capture(), any());
        String redisKey = keyCaptor.getValue().getFirst();
        org.junit.jupiter.api.Assertions.assertFalse(redisKey.contains("01012345678"));
    }
}
