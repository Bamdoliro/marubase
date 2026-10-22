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

@RequiredArgsConstructor
@UseCase
public class ExportApplicationFormUseCase {

    private final FormRepository formRepository;
    private final ApplicationFormXlsxWriter applicationFormXlsxWriter;

    public Resource execute() throws IOException {
        List<Form> formList = formRepository.findByStatus(FormStatus.APPROVED).stream()
                .sorted(Comparator.comparing(Form::getId))
                .toList();

        return applicationFormXlsxWriter.write(formList);
    }
}
