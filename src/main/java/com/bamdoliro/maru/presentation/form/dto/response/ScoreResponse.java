package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.value.Score;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public class ScoreResponse {

    private Double firstRoundScore;
    private Double totalScore;

    public ScoreResponse(Score score) {
        this.firstRoundScore = round(score.getFirstRoundScore());
        this.totalScore = round(score.getTotalScore());
    }

    private Double round(Double value){
        if(value == null){
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
