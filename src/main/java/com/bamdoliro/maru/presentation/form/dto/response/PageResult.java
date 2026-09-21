package com.bamdoliro.maru.presentation.form.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResult<T> {
    List<T> data;
    long totalCount;
    long totalPages;
}
