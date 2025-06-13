package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormResponse {
    private Long id;
    private Long examinationNumber;
    private ApplicantResponse applicant;
    private ParentResponse parent;
    private EducationResponse education;
    private GradeResponse grade;
    private DocumentResponse document;
    private ScoreResponse score;
    private String formUrl;
    private FormType type;
    private FormStatus status;
    private boolean isChangedToRegular;

    public FormResponse(Form form, String identificationPictureUri, String formUrl) {
        this.id = form.getId();
        this.examinationNumber = form.getExaminationNumber();
        this.applicant = new ApplicantResponse(form.getApplicant(), identificationPictureUri);
        this.parent = new ParentResponse(form.getParent());
        this.education = new EducationResponse(form.getEducation());
        this.grade = new GradeResponse(form.getGrade());
        this.document = new DocumentResponse(form.getDocument());
        this.score = new ScoreResponse(form.getScore());
        this.formUrl = formUrl;
        this.type = form.getType();
        this.status = form.getStatus();
        this.isChangedToRegular = form.getChangedToRegular();
    }
}
