package com.bamdoliro.maru.shared.config;

import com.bamdoliro.maru.shared.config.properties.JasyptProperties;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.salt.StringFixedSaltGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JasyptConfig {

    private final JasyptProperties jasyptProperties;

    @Bean
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setPassword(jasyptProperties.getKey());
        encryptor.setAlgorithm("PBEWithSHA256And256BitAES-CBC-BC");
        encryptor.setKeyObtentionIterations(1000);
        encryptor.setPoolSize(1);

        encryptor.setSaltGenerator(new StringFixedSaltGenerator(jasyptProperties.getSalt()));
        return encryptor;
    }

}
