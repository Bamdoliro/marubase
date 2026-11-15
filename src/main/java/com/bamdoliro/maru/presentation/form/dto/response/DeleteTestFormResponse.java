package com.bamdoliro.maru.presentation.form.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteTestFormResponse {

    private int deletedFormCount;
    private int deletedUserCount;
}
