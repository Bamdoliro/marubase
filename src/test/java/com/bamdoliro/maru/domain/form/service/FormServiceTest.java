package com.bamdoliro.maru.domain.form.service;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

    @InjectMocks
    private FormService formService;

    @Test
    void 일반전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createForm(FormType.REGULAR);

        // when
        formService.calculateScore(form);

        // then
        assertEquals(195.886, form.getScore().getSubjectGradeScore());
        assertEquals(15, form.getScore().getAttendanceScore());
        assertEquals(16, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 특별전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createForm(FormType.MEISTER_TALENT);

        // when
        formService.calculateScore(form);

        // then
        assertEquals(117.531, form.getScore().getSubjectGradeScore());
        assertEquals(15, form.getScore().getAttendanceScore());
        assertEquals(16, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 검정고시_일반전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createQualificationExaminationForm(FormType.REGULAR);

        // when
        formService.calculateScore(form);

        // then
        assertEquals(156.000, form.getScore().getSubjectGradeScore());
        assertEquals(14, form.getScore().getAttendanceScore());
        assertEquals(14, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }

    @Test
    void 검정고시_특별전형_1차_점수를_계산한다() {
        // given
        Form form = FormFixture.createQualificationExaminationForm(FormType.MEISTER_TALENT);

        // when
        formService.calculateScore(form);

        // then
        // TODO :: 검정고시 특별전형 점수 계산 틀림 57.120
        assertEquals(93.6, form.getScore().getSubjectGradeScore());
        assertEquals(14, form.getScore().getAttendanceScore());
        assertEquals(14, form.getScore().getVolunteerScore());
        assertEquals(2, form.getScore().getBonusScore());
    }
}