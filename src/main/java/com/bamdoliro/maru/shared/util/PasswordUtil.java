package com.bamdoliro.maru.shared.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean matches(String actual, String expected) {
        return passwordEncoder.matches(expected, actual);
    }

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
