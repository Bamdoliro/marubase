package com.bamdoliro.maru.domain.form.domain.value;

import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GradeTest {

    @Test
    void 자격증을_중복_소지하고_있을_경우_최고_수준의_자격증_한_개만_인정한다() {
        // given
        List<Certificate> certificateList = List.of(
                Certificate.CRAFTSMAN_COMPUTER,
                Certificate.CRAFTSMAN_INFORMATION_EQUIPMENT_OPERATION,
                Certificate.COMPUTER_SPECIALIST_LEVEL_2,
                Certificate.COMPUTER_SPECIALIST_LEVEL_3
        );

        // when
        Grade grade = new Grade(null, null, null, null, null, null, null, new CertificateList(certificateList));

        // then
        Certificate computerSpecialistCertificate = grade.getCertificateListValue().stream()
                .filter(Certificate::isComputerSpecialist)
                .findFirst()
                .get();

        assertEquals(3, grade.getCertificateList().size());
        assertEquals(Certificate.COMPUTER_SPECIALIST_LEVEL_2, computerSpecialistCertificate);
    }
}