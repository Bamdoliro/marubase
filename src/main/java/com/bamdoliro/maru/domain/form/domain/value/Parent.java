package com.bamdoliro.maru.domain.form.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Parent {

    @Column(name = "parent_name", nullable = false, length = 20)
    private String name;

    @Column(name = "parent_phone_number", nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "parent_relation", nullable = false, length = 20)
    private String relation;

    @Embedded
    private Address address;
}
