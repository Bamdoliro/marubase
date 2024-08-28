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

    @Column(name = "school_name", nullable = true, length = 21)
    private String name;

    @Column(name = "school_location", nullable = true, length = 20)
    private String location;

    @Column(name = "school_address", nullable = true, length = 40)
    private String address;

    @Column(name = "school_code", nullable = true, length = 10)
    private String code;

    public boolean isBusan() {
        return location.equals("부산광역시");
    }
}