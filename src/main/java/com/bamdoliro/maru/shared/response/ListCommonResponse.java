package com.bamdoliro.maru.shared.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ListCommonResponse<T> extends CommonResponse {
    List<T> dataList;

    public ListCommonResponse(String code, String message, List<T> dataList) {
        super(code, message);
        this.dataList = dataList;
    }
}
