package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.form.domain.value.Education;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EducationResponse {

    private GraduationType graduationType;
    private String graduationYear;
    private String schoolName;
    private String schoolLocation;
    private String schoolCode;
    private String teacherName;
    private String teacherPhoneNumber;

    public EducationResponse(Education education) {
        this.graduationType = education.getGraduationType();
        this.graduationYear = education.getGraduationYear();
        this.schoolName = education.getSchool().getName();
        this.schoolLocation = education.getSchool().getLocation();
        this.schoolCode = education.getSchool().getCode();
        this.teacherName = education.getTeacher().getName();
        this.teacherPhoneNumber = education.getTeacher().getPhoneNumber().toString();
    }
}
