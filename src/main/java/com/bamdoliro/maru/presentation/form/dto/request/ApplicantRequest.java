package com.bamdoliro.maru.presentation.form.dto.request;

import com.bamdoliro.maru.domain.form.domain.type.Gender;
import com.bamdoliro.maru.domain.form.domain.value.Applicant;
import com.bamdoliro.maru.domain.form.domain.value.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 20, message = "20자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "필수값입니다.")
    @Size(min = 11, max = 11, message = "11자여야 합니다.")
    private String phoneNumber;

    @NotNull(message = "필수값입니다.")
    @Past(message = "과거여야 합니다.")
    private LocalDate birthday;

    @NotNull(message = "필수값입니다.")
    private Gender gender;

    public Applicant toValue() {
        return new Applicant(
                name,
                new PhoneNumber(phoneNumber),
                birthday,
                gender
        );
    }
}
