package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.ApplicationFormXlsxWriter;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@UseCase
public class ExportApplicationFormUseCase {

    // TODO: 내보낼 원서 상태 확정 필요 (입학처 확인)
    private static final Set<FormStatus> TARGET_STATUS = Set.of(FormStatus.PASSED, FormStatus.ENTERED);

    private final FormFacade formFacade;
    private final ApplicationFormXlsxWriter applicationFormXlsxWriter;

    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(null)
                .stream()
                .filter(form -> TARGET_STATUS.contains(form.getStatus()))
                .toList();

        return applicationFormXlsxWriter.write(formList);
    }
}
