package com.bamdoliro.maru.presentation.notice.dto.request;

import com.bamdoliro.maru.domain.question.domain.type.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoticeRequest {
    @NotBlank(message = "필수값입니다.")
    @Size(max = 64, message = "64글자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 1024, message = "1024글자 이하여야 합니다.")
    private String content;

    @NotBlank(message = "카테고리를 선택해야합니다.")
    private Category category;
}
