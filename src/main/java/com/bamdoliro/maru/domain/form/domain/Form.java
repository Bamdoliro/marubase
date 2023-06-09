package com.bamdoliro.maru.domain.form.domain;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Applicant;
import com.bamdoliro.maru.domain.form.domain.value.Document;
import com.bamdoliro.maru.domain.form.domain.value.Education;
import com.bamdoliro.maru.domain.form.domain.value.Grade;
import com.bamdoliro.maru.domain.form.domain.value.Parent;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_form")
@Entity
public class Form extends BaseTimeEntity {

    @Column(name = "form_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private UUID uuid;

    @Embedded
    private Applicant applicant;

    @Embedded
    private Parent parent;

    @Embedded
    private Education education;

    @Embedded
    private Grade grade;

    @Embedded
    private Document document;

    @Embedded
    private Score score;

    @Column(nullable = false, length = 150)
    private String formUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormStatus status;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder
    public Form(Applicant applicant, Parent parent, Education education, Grade grade, Document document, String formUrl, FormType type, User user) {
        this.applicant = applicant;
        this.parent = parent;
        this.education = education;
        this.grade = grade;
        this.document = document;
        this.formUrl = formUrl;
        this.type = type;
        this.user = user;
        this.uuid = UUID.randomUUID();
        this.status = FormStatus.SUBMITTED;
    }

    public void updateScore(Score score) {
        this.score = score;
    }

    public void approve() {
        this.status = FormStatus.APPROVED;
    }

    public void reject() {
        this.status = FormStatus.REJECTED;
    }

    public void isApplicant(User user) {
        if (!this.user.equals(user)) {
            throw new AuthorityMismatchException();
        }
    }

    public void isApplicantOrAdmin(User user) {
        if (!user.isAdmin() && !this.user.equals(user)) {
            throw new AuthorityMismatchException();
        }
    }

    public boolean isRejected() {
        return status.equals(FormStatus.REJECTED);
    }

    public void update(Applicant applicant, Parent parent, Education education, Grade grade, Document document, String formUrl, FormType type) {
        this.applicant = applicant;
        this.parent = parent;
        this.education = education;
        this.grade = grade;
        this.document = document;
        this.formUrl = formUrl;
        this.type = type;
        this.status = FormStatus.SUBMITTED;
    }
}

