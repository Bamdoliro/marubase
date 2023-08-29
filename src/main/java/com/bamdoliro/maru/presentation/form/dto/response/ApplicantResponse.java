package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.Gender;
import com.bamdoliro.maru.domain.form.domain.value.Applicant;
import com.bamdoliro.maru.domain.form.domain.value.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ApplicantResponse {

    private String identificationPictureUri;
    private String name;
    private String phoneNumber;
    private LocalDate birthday;
    private Gender gender;

    public ApplicantResponse(Applicant applicant) {
        this.identificationPictureUri = applicant.getIdentificationPictureUri();
        this.name = applicant.getName();
        this.phoneNumber = applicant.getPhoneNumber().toString();
        this.birthday = applicant.getBirthday();
        this.gender = applicant.getGender();
    }
}
