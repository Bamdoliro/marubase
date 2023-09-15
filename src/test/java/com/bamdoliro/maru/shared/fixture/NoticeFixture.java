package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.response.IdResponse;

public class NoticeFixture {

    public static Notice createNotice() {
        return new Notice("잔류되나요?", "왜자꾸주말에 기숙사에서 잔류규 하시쥬?");
    }

    public static NoticeSimpleResponse createNoticeSimpleResponse() {
        return new NoticeSimpleResponse(createNotice());
    }
}
