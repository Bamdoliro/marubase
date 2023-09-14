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
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_form")
@Entity
public class Form extends BaseTimeEntity {

    @Column(name = "form_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = true, unique = true)
    private Long examinationNumber;

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

    @Column(nullable = true, length = 150)
    private String formUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormType type;

    @Column(nullable = false)
    private Boolean changedToRegular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FormStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Builder
    public Form(Applicant applicant, Parent parent, Education education, Grade grade, Document document, FormType type, User user) {
        this.applicant = applicant;
        this.parent = parent;
        this.education = education;
        this.grade = grade;
        this.document = document;
        this.type = type;
        this.user = user;
        this.changedToRegular = false;
        this.status = FormStatus.SUBMITTED;
    }

    public void submit(String formUrl) {
        this.formUrl = formUrl;
        this.status = FormStatus.FINAL_SUBMITTED;
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

    public void receive() {
        this.status = FormStatus.RECEIVED;
    }

    public void firstPass() {
        this.status = FormStatus.FIRST_PASSED;
    }

    public void firstFail() {
        this.status = FormStatus.FIRST_FAILED;
    }

    public void pass() {
        this.status = FormStatus.PASSED;
    }

    public void fail() {
        this.status = FormStatus.FAILED;
    }

    public void noShow() {
        this.status = FormStatus.NO_SHOW;
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

    public boolean isSubmitted() {
        return status.equals(FormStatus.SUBMITTED);
    }

    public boolean isReceived() {
        return isFirstPassed() != null || status.equals(FormStatus.RECEIVED);
    }

    public boolean isNoShow() {
        return status.equals(FormStatus.NO_SHOW);
    }

    public boolean isFirstPassedNow() {
        return status.equals(FormStatus.FIRST_PASSED);
    }

    public Boolean isFirstPassed() {
        if (isFirstPassedNow() || isPassed() != null || isNoShow()) {
            return true;
        }

        return isFirstFailedNow() ? false : null;
    }

    public boolean isFirstFailedNow() {
        return status.equals(FormStatus.FIRST_FAILED);
    }

    public boolean isPassedNow() {
        return status.equals(FormStatus.PASSED);
    }

    public Boolean isPassed() {
        if (isPassedNow()) {
            return true;
        }

        return isFailedNow() || isNoShow() ? false : null;
    }

    public boolean isFailedNow() {
        return status.equals(FormStatus.FAILED);
    }

    public void changeToRegular(CalculateFormScoreService calculateFormScoreService) {
        this.changedToRegular = true;

        Double subjectGradeScore = calculateFormScoreService.calculateSubjectGradeScore(this);
        this.score.updateSubjectScore(subjectGradeScore);
    }

    public void update(Applicant applicant, Parent parent, Education education, Grade grade, Document document, FormType type) {
        this.applicant = applicant;
        this.parent = parent;
        this.education = education;
        this.grade = grade;
        this.document = document;
        this.type = type;
        this.status = FormStatus.FINAL_SUBMITTED;
    }

    public void assignExaminationNumber(Long examinationNumber) {
        this.examinationNumber = examinationNumber;
    }

    public FormType getType() {
        if (changedToRegular) {
            return FormType.REGULAR;
        }

        return type;
    }

    public FormType getOriginalType() {
        return type;
    }

    public boolean tookSecondRound() {
        return status.equals(FormStatus.PASSED) || status.equals(FormStatus.FAILED);
    }
}

