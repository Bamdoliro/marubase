package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.value.Score;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreResponse {

    private Double firstRoundScore;
    private Double totalScore;

    public ScoreResponse(Score score) {
        this.firstRoundScore = score.getFirstRoundScore();
        this.totalScore = score.getTotalScore();
    }
}
