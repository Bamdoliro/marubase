package com.bamdoliro.maru.domain.form.domain.value;

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
public class School {

    @Column(name = "school_name", nullable = false, length = 20)
    private String name;

    @Column(name = "school_location", nullable = false, length = 20)
    private String location;

    @Column(name = "school_address", nullable = false, length = 40)
    private String address;

    @Column(name = "school_code", nullable = false, length = 10)
    private String code;

    public boolean isBusan() {
        return location.equals("부산광역시");
    }
}