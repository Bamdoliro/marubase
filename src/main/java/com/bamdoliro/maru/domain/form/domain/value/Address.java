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
public class Address {

    @Column(name = "zone_code", nullable = false, length = 5)
    private String zoneCode;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "detail_address", nullable = false, length = 100)
    private String detailAddress;
}
