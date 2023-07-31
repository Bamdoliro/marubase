package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.question.domain.Question;
import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;

public class QuestionFixture {

    public static Question createQuestion() {
        return new Question("급식 맛있나요?", "오늘 저녁 돼지국밥!!!!!!", QuestionCategory.TOP_QUESTION);
    }

}
