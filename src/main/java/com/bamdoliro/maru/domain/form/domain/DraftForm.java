package com.bamdoliro.maru.domain.form.domain;

import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "formDraft", timeToLive = 60 * 60 * 24 * 7)
public class DraftForm {

    @Id
    private String phoneNumber;

    private SubmitFormRequest submitFormRequest;

    @Builder
    public DraftForm(String phoneNumber, SubmitFormRequest submitFormRequest) {
        this.phoneNumber = phoneNumber;
        this.submitFormRequest = submitFormRequest;
    }
}
