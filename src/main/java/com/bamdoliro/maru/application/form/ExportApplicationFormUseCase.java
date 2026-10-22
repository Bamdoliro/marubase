package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.ApplicationFormXlsxWriter;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@UseCase
public class ExportApplicationFormUseCase {

    // 승인되었거나 1차 결과가 나온 지원자
    private static final Set<FormStatus> TARGET_STATUS_SET = Set.of(
            FormStatus.APPROVED,
            FormStatus.FIRST_PASSED,
            FormStatus.FIRST_FAILED
    );

    private final FormRepository formRepository;
    private final ApplicationFormXlsxWriter applicationFormXlsxWriter;

    public Resource execute() throws IOException {
        List<Form> formList = formRepository.findByStatusIn(TARGET_STATUS_SET).stream()
                .sorted(Comparator.comparing(Form::getId))
                .toList();

        return applicationFormXlsxWriter.write(formList);
    }
}
