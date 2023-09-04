package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CertificateList {

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_certificate",
            joinColumns = @JoinColumn(name = "form_id"))
    @Column(nullable = false, name = "certificate")
    @Enumerated(EnumType.STRING)
    private List<Certificate> value;

    public CertificateList(List<Certificate> value) {
        this.value = validate(value);
    }

    @Override
    public String toString() {
        return value.stream()
                .map(Certificate::toString)
                .collect(Collectors.joining(", "));
    }

    public int size() {
        return value.size();
    }

    private List<Certificate> validate(List<Certificate> certificateList) {
        if (Objects.nonNull(certificateList) && !certificateList.isEmpty()) {
            Optional<Certificate> highestScoreComputerSpecialistCertificate = certificateList.stream()
                    .filter(Certificate::isComputerSpecialist)
                    .max(Comparator.comparingInt(Certificate::getScore));

            if (highestScoreComputerSpecialistCertificate.isPresent()) {
                List<Certificate> validatedCertificateList = new ArrayList<>(
                        certificateList.stream()
                                .filter((c) -> !c.isComputerSpecialist())
                                .toList());

                validatedCertificateList.add(highestScoreComputerSpecialistCertificate.get());
                return validatedCertificateList;
            }
        }

        return certificateList;
    }
}
