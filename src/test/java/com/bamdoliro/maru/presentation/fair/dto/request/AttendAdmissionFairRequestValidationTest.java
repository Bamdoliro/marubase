package com.bamdoliro.maru.presentation.fair.dto.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;

class AttendAdmissionFairRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 여러_언어와_일반적인_이름_표기를_허용한다() {
        List<String> names = List.of(
                "김한울",
                "Nguyễn Văn An",
                "মোঃ রহমান",
                "Md. Rahman",
                "O'Brien",
                "Jean-Luc"
        );

        names.forEach(name -> assertTrue(
                validator.validate(createRequest(name, "01012345678", 2)).isEmpty(),
                name
        ));
    }

    @Test
    void 링크나_줄바꿈으로_오인될_수_있는_이름을_거부한다() {
        List<String> names = List.of(
                "bit.ly",
                "홍길동\n원서 반려",
                "홍길동123",
                "홍길동/관리자",
                "@admin"
        );

        names.forEach(name -> assertFalse(
                validator.validate(createRequest(name, "01012345678", 2)).isEmpty(),
                name
        ));
    }

    @Test
    void 휴대전화번호와_신청_인원_범위를_검증한다() {
        assertFalse(validator.validate(createRequest("김한울", "01112345678", 2)).isEmpty());
        assertFalse(validator.validate(createRequest("김한울", "0101234abcd", 2)).isEmpty());
        assertFalse(validator.validate(createRequest("김한울", "01012345678", 11)).isEmpty());
    }

    private AttendAdmissionFairRequest createRequest(String name, String phoneNumber, Integer headcount) {
        return new AttendAdmissionFairRequest(
                "비전중학교",
                name,
                "학생",
                phoneNumber,
                headcount,
                null
        );
    }
}
