package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.value.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentResponse {

    private String coverLetter;
    private String statementOfPurpose;

    public DocumentResponse(Document document) {
        this.coverLetter = document.getCoverLetter();
        this.statementOfPurpose = document.getStatementOfPurpose();
    }
}
