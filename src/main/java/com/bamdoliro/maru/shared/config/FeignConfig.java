package com.bamdoliro.maru.shared.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.bamdoliro.maru.infrastructure")
@Configuration
public class FeignConfig {
}
