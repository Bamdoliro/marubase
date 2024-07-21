package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.application.notice.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeResponse;
import com.bamdoliro.maru.presentation.notice.dto.response.NoticeSimpleResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.IdResponse;
import com.bamdoliro.maru.shared.response.ListCommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/notice")
@RestController
public class NoticeController {

    private final CreateNoticeUseCase createNoticeUseCase;
    private final UpdateNoticeUseCase updateNoticeUseCase;
    private final QueryNoticeListUseCase queryNoticeListUseCase;
    private final QueryNoticeUseCase queryNoticeUseCase;
    private final DeleteNoticeUseCase deleteNoticeUseCase;
    private final UploadFileUseCase uploadFileUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SingleCommonResponse<IdResponse> createNotice(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid NoticeRequest request
    ) {
        return CommonResponse.ok(
                createNoticeUseCase.execute(request));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/file")
    public SingleCommonResponse<UploadResponse> uploadFile(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestPart("file") MultipartFile file
    ) {
        return SingleCommonResponse.ok(
                uploadFileUseCase.execute(user, file)
        );
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
