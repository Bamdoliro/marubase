package com.bamdoliro.maru.shared.response;

import java.util.List;

public class ListCommonResponse<T> extends CommonResponse {
    List<T> dataList;

    public ListCommonResponse(String code, String message, List<T> dataList) {
        super(code, message);
        this.dataList = dataList;
    }
}
