package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreResponse {

    private Double firstRoundScore;
    private Double totalScore;

    public ScoreResponse(Score score) {
        this.firstRoundScore = MathUtil.roundTo(score.getFirstRoundScore(), 3);
        this.totalScore = MathUtil.roundTo(score.getTotalScore(), 3);
    }

}
