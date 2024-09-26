package com.bamdoliro.maru.presentation.notice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileRequest {

    @NotEmpty(message = "필수값입니다.")
    @Size(max = 3, message = "최대 3개의 파일만 업로드할 수 있습니다.")
    private List<String> fileNameList;
}
