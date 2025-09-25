package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;


@RequiredArgsConstructor
@UseCase
public class ExportFirstScoreUseCase {

    private final FormFacade formFacade;
    private final XlsxGenerator xlsxGenerator;

    @Transactional(readOnly = true)
    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(null);

        List<Function<Form, Object>> columnList = List.of(
                Form::getId,
                Form::getExaminationNumber,
                form -> form.getOriginalType().getDescription(),
                form -> form.getType().getDescription(),
                form -> form.getStatus().getDescription(),
                form -> form.getApplicant().getName(),
                form -> form.getApplicant().getGender().getDescription(),
                form -> form.getApplicant().getBirthday().format(DateTimeFormatter.BASIC_ISO_DATE),
                form -> form.getEducation().getSchool().getLocation(),
                form -> form.getEducation().getGraduationTypeToString(),
                form -> form.getEducation().getSchool().getName(),
                form -> form.getEducation().getSchool().getCode(),
                form -> MathUtil.roundTo(form.getScore().getSubjectGradeScore(), 3),
                form -> form.tookSecondRound() ? MathUtil.roundTo(form.getScore().getTotalScore(), 3) : MathUtil.roundTo(form.getScore().getFirstRoundScore(), 3)
        );

        List<String> styleList = List.of(
                "default",
                "default",
                "default",
                "default",
                "default",
                "default",
                "default",
                "default",
                "default",
                "right",
                "right",
                "right",
                "right",
                "right"
        );

        return xlsxGenerator.execute("1차원서점수", formList, columnList, styleList);
    }

}
