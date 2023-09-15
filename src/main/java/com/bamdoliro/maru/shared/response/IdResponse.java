package com.bamdoliro.maru.shared.response;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.domain.question.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdResponse {

    private Long id;

    public IdResponse(Notice notice) {
        this.id = notice.getId();
    }

    public IdResponse(Question question) {
        this.id = question.getId();
    }
}
