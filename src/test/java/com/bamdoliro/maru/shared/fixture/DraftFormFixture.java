package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.form.domain.DraftForm;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.presentation.form.dto.request.SubmitFormRequest;

public class DraftFormFixture {

    public static DraftForm createDraftForm() {
        return new DraftForm(
                "01085852525",
                FormFixture.createFormRequest(FormType.REGULAR)
        );
    }

    public static SubmitFormRequest createDraftFormRequest() {
        return new SubmitFormRequest(
                FormFixture.createApplicantRequest(),
                FormFixture.createParentRequest(),
                null,
                null,
                null,
                FormType.REGULAR
        );
    }
}
