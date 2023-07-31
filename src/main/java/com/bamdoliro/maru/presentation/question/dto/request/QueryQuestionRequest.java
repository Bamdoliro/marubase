package com.bamdoliro.maru.presentation.question.dto.request;

import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryQuestionRequest {
    private int page;
    private int size;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 64, message = "64글자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "필수값입니다.")
    @Size(max = 1024, message = "1024글자 이하여야 합니다.")
    private String content;

    @NotNull(message = "카테고리를 선택해야합니다.")
    private QuestionCategory category;
    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

}