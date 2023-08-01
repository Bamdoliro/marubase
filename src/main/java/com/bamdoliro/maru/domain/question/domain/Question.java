package com.bamdoliro.maru.domain.question.domain;

import com.bamdoliro.maru.domain.question.domain.type.QuestionCategory;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_question")
@Entity
public class Question extends BaseTimeEntity {

    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 1024)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionCategory category;

    @Builder
    public Question(String title, String content, QuestionCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void update(String title, String content, QuestionCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
