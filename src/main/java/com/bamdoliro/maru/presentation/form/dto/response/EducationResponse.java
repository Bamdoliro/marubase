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
    private String schoolPhoneNumber;
    private String schoolAddress;
    private String teacherName;
    private String teacherMobilePhoneNumber;

    public EducationResponse(Education education) {
        this.graduationType = education.getGraduationType();
        this.graduationYear = education.getGraduationYear();
        this.schoolName = education.getSchool().getName();
        this.schoolLocation = education.getSchool().getLocation();
        this.schoolAddress = education.getSchool().getAddress();
        this.schoolCode = education.getSchool().getCode();
        this.schoolPhoneNumber = education.getTeacher().getPhoneNumber().toString().isEmpty() ? null : education.getTeacher().getPhoneNumber().toString();
        this.teacherName = education.getTeacher().getName();
        this.teacherMobilePhoneNumber = education.getTeacher().getMobilePhoneNumber().toString().isEmpty() ? null : education.getTeacher().getMobilePhoneNumber().toString();
    }
}
