package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.application.notice.*;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/notices")
@RestController
public class NoticeController {

    private final QueryNoticeListUseCase queryNoticeListUseCase;
    private final QueryNoticeUseCase queryNoticeUseCase;

    @GetMapping
    public ListCommonResponse<NoticeSimpleResponse> queryNoticeList() {
        return CommonResponse.ok(
                queryNoticeListUseCase.execute()
        );
    }

    @GetMapping("/{notice-id}")
    public SingleCommonResponse<NoticeResponse> queryNotice(
            @PathVariable(name = "notice-id") Long noticeId
    ) {
        return CommonResponse.ok(
                queryNoticeUseCase.execute(noticeId)
        );
    }

}
