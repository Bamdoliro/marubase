package com.bamdoliro.maru.shared.config;

import com.bamdoliro.maru.shared.config.properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class MessageConfig {

    private final MessageProperties messageProperties;

    @Bean
    public DefaultMessageService initMessageService() {
        return NurigoApp.INSTANCE.initialize(
                messageProperties.getApiKey(),
                messageProperties.getApiSecret(),
                messageProperties.getApiDomain()
        );
    }
}
