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
public class Teacher {

    @Column(name = "teacher_name", nullable = true, length = 20)
    private String name;

    @Column(name = "teacher_phone_number", nullable = true, length = 11)
    private String phoneNumber;
}
