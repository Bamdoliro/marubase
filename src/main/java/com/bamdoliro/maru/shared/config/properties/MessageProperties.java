package com.bamdoliro.maru.shared.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("message")
public class MessageProperties {

    private String apiKey;
    private String apiSecret;
    private String apiDomain;
    private String from;
}