package com.bamdoliro.maru.presentation.analysis.dto.response;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenderRatioResponse {
    private FormType.Category category;
    private long  busanMale;
    private long  busanFemale;
    private long otherLocationMale;
    private long otherLocationFemale;
}