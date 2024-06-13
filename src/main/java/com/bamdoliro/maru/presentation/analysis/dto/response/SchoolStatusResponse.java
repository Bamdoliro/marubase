package com.bamdoliro.maru.presentation.analysis.dto.response;

import com.bamdoliro.maru.infrastructure.persistence.form.vo.SchoolStatusVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolStatusResponse {
    private String applicantName;
    private String schoolName;
    private String schoolAddress;

    public SchoolStatusResponse(SchoolStatusVo schoolStatusVo) {
        this.applicantName = schoolStatusVo.getApplicantName();
        this.schoolName = schoolStatusVo.getSchoolName();
        this.schoolAddress = schoolStatusVo.getSchoolAddress();
    }
}