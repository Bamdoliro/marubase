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
    private ApplicantResponse applicant;
    private ParentResponse parent;
    private EducationResponse education;
    private GradeResponse grade;
    private DocumentResponse document;
    private String formUrl;
    private FormType type;
    private FormStatus status;

    public FormResponse(Form form) {
        this.id = form.getId();
        this.applicant = new ApplicantResponse(form.getApplicant());
        this.parent = new ParentResponse(form.getParent());
        this.education = new EducationResponse(form.getEducation());
        this.grade = new GradeResponse(form.getGrade());
        this.document = new DocumentResponse(form.getDocument());
        this.formUrl = form.getFormUrl();
        this.type = form.getType();
        this.status = form.getStatus();
    }
}
