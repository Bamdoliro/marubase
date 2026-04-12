package com.bamdoliro.maru.presentation.fair;

import com.bamdoliro.maru.application.fair.*;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.presentation.fair.dto.request.CreateFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.request.UpdateFairRequest;
import com.bamdoliro.maru.presentation.fair.dto.response.FairDetailResponse;
import com.bamdoliro.maru.shared.auth.AuthenticationPrincipal;
import com.bamdoliro.maru.shared.auth.Authority;
import com.bamdoliro.maru.shared.response.CommonResponse;
import com.bamdoliro.maru.shared.response.SingleCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/fairs")
public class AdminFairController {

    private final CreateAdmissionFairUseCase createAdmissionFairUseCase;
    private final UpdateAdmissionFairUseCase updateAdmissionFairUseCase;
    private final DeleteAdmissionFairUseCase deleteAdmissionFairUseCase;
    private final QueryFairDetailUseCase queryFairDetailUseCase;
    private final ExportAttendeeListUseCase exportAttendeeListUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createAdmissionFair(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @RequestBody @Valid CreateFairRequest request
    ) {
        createAdmissionFairUseCase.execute(request);
    }

    @GetMapping("/{fair-id}")
    public SingleCommonResponse<FairDetailResponse> getFairDetail(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "fair-id") Long fairId
    ) {
        return CommonResponse.ok(
                queryFairDetailUseCase.execute(fairId)
        );
    }

    @GetMapping("/{fair-id}/export")
    public ResponseEntity<Resource> exportFairAttendeeList(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "fair-id") Long fairId
    ) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportAttendeeListUseCase.execute(fairId));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{fair-id}")
    public void updateQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "fair-id") Long fairId,
            @RequestBody @Valid UpdateFairRequest request
    ) {
        updateAdmissionFairUseCase.execute(fairId, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{fair-id}")
    public void deleteQuestion(
            @AuthenticationPrincipal(authority = Authority.ADMIN) User user,
            @PathVariable(name = "fair-id") Long fairId
    ) {
        deleteAdmissionFairUseCase.execute(fairId);
    }

}
