package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.shared.response.IdResponse;

public class SharedFixture {

    public static IdResponse createIdResponse() {
        return new IdResponse(1L);
    }
}
