package com.bamdoliro.maru.shared.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("spring.jasypt.encryptor")
public class JasyptProperties {

    private String key;

    private String salt;

}
