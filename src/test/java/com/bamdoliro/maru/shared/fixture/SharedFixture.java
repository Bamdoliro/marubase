package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.shared.response.IdResponse;

public class SharedFixture {

    public static IdResponse createIdResponse() {
        return new IdResponse(1L);
    }

    public static UrlResponse createNoticeFileUrlResponse() {
        return new UrlResponse(
            "https://maru.bamdoliro.com/notice-file.pdf",
            "https://maru.bamdoliro.com/notice-file.pdf"
        );
    }

    public static UrlResponse createIdentificationPictureUrlResponse() {
        return new UrlResponse(
                "https://maru.bamdoliro.com/identification-picture.png",
                "https://maru.bamdoliro.com/identification-picture.png"
        );
    }

    public static UrlResponse createFormUrlResponse() {
        return new UrlResponse(
                "https://maru.bamdoliro.com/form.pdf",
                "https://maru.bamdoliro.com/form.pdf"
        );
    }

    public static UrlResponse createAdmissionAndPledgeUrlResponse() {
        return new UrlResponse(
                "https://maru.bamdoliro.com/admission-and-pledge.pdf",
                "https://maru.bamdoliro.com/admission-and-pledge.pdf"
        );
    }
}
