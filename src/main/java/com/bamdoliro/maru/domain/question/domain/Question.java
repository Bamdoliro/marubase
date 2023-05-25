package com.bamdoliro.maru.domain.question.domain;

import com.bamdoliro.maru.presentation.question.dto.request.UpdateQuestionRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Question {

    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 1024)
    private String content;

    @Builder
    public Question(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
