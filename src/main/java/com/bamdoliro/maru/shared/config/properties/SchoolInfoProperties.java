package com.bamdoliro.maru.shared.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("school-info")
public class SchoolInfoProperties {

    private String key;
}