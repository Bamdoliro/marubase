package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.application.notice.CreateNoticeUseCase;
import com.bamdoliro.maru.application.notice.DeleteNoticeUseCase;
import com.bamdoliro.maru.application.notice.QueryNoticeListUseCase;
import com.bamdoliro.maru.application.notice.QueryNoticeUseCase;
import com.bamdoliro.maru.application.notice.UpdateNoticeUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/notice")
@RestController
public class NoticeController {

    private final CreateNoticeUseCase createNoticeUseCase;
    private final UpdateNoticeUseCase updateNoticeUseCase;
    private final QueryNoticeListUseCase queryNoticeListUseCase;
    private final QueryNoticeUseCase queryNoticeUseCase;
    private final DeleteNoticeUseCase deleteNoticeUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createNotice(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid NoticeRequest request
    ) {
        createNoticeUseCase.execute(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{notice-id}")
    public void updateNotice(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "notice-id") Long noticeId,
            @RequestBody @Valid NoticeRequest request
    ) {
        updateNoticeUseCase.execute(noticeId, request);
    }

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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{notice-id}")
    public void deleteNotice(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "notice-id") Long noticeId
    ) {
        deleteNoticeUseCase.execute(noticeId);
    }
}
