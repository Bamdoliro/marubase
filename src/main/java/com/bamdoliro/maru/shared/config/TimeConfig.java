package com.bamdoliro.maru.shared.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    public static final ZoneId SERVICE_ZONE_ID = ZoneId.of("Asia/Seoul");

    @Bean
    public Clock serviceClock() {
        return Clock.system(SERVICE_ZONE_ID);
    }
}
