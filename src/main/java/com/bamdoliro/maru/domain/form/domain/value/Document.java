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
public class Document {

    @Column(nullable = false, length = 1600)
    private String coverLetter;

    @Column(nullable = false, length = 1600)
    private String statementOfPurpose;
}
