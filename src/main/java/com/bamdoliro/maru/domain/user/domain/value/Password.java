package com.bamdoliro.maru.domain.user.domain.value;

import com.bamdoliro.maru.shared.util.PasswordUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Password {

    @Column(name = "password", nullable = false, length = 60)
    private String value;

    public boolean match(String password) {
        return PasswordUtil.matches(value, password);
    }
}
