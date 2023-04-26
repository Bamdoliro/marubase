package com.bamdoliro.maru.shared.config;

import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class})
public class PropertiesConfig {
}