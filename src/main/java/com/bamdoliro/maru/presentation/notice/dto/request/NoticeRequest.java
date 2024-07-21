package com.bamdoliro.maru.presentation.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequest {

    @NotBlank(message = "필수값입니다.")
    @Size(max = 64, message = "64글자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 1024, message = "1024글자 이하여야 합니다.")
    private String content;

    @Size(max = 150, message = "150자 이하여야합니다.")
    @URL(message = "올바른 URL 형식이어야 합니다.")
    private String fileUrl;
}
