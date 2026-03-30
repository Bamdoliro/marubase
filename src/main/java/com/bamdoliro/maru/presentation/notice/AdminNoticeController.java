package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.application.notice.CreateNoticeUseCase;
import com.bamdoliro.maru.application.notice.DeleteNoticeUseCase;
import com.bamdoliro.maru.application.notice.UpdateNoticeUseCase;
import com.bamdoliro.maru.application.notice.UploadFileUseCase;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.UploadFileResponse;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/notices")
public class AdminNoticeController {

    private final CreateNoticeUseCase createNoticeUseCase;
    private final UploadFileUseCase uploadFileUseCase;
    private final UpdateNoticeUseCase updateNoticeUseCase;
    private final DeleteNoticeUseCase deleteNoticeUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public SingleCommonResponse<IdResponse> createNotice(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid NoticeRequest request
    ) {
        return CommonResponse.ok(
                createNoticeUseCase.execute(request));
    }

    @PostMapping("/files")
    public ListCommonResponse<UploadFileResponse> uploadFile(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid List<FileMetadata> metadataList
    ) {
        return SingleCommonResponse.ok(
                uploadFileUseCase.execute(metadataList)
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{notice-id}")
    public void deleteNotice(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "notice-id") Long noticeId
    ) {
        deleteNoticeUseCase.execute(noticeId);
    }

}
