package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.FormColumn;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor
@UseCase
public class ExportFirstScoreUseCase {

    private final FormFacade formFacade;
    private final XlsxGenerator xlsxGenerator;

    @Transactional(readOnly = true)
    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(null);

        List<FormColumn> columnList = List.of(
                FormColumn.text(Form::getId),
                FormColumn.text(Form::getExaminationNumber),
                FormColumn.text(form -> form.getOriginalType().getDescription()),
                FormColumn.text(form -> form.getType().getDescription()),
                FormColumn.text(form -> form.getStatus().getDescription()),
                FormColumn.text(form -> form.getApplicant().getName()),
                FormColumn.text(form -> form.getApplicant().getGender().getDescription()),
                FormColumn.text(form -> form.getApplicant().getBirthday().format(DateTimeFormatter.BASIC_ISO_DATE)),
                FormColumn.text(form -> form.getEducation().getSchool().getLocation()),
                FormColumn.text(form -> form.getEducation().getGraduationTypeToString()),
                FormColumn.text(form -> form.getEducation().getSchool().getName()),
                FormColumn.text(form -> form.getEducation().getSchool().getCode()),
                FormColumn.score( form -> MathUtil.roundTo(form.getScore().getSubjectGradeScore(), 3)),
                FormColumn.score(form -> form.tookSecondRound() ? MathUtil.roundTo(form.getScore().getTotalScore(), 3) : MathUtil.roundTo(form.getScore().getFirstRoundScore(), 3))
        );

        return xlsxGenerator.execute("1차원서점수", formList, columnList);
    }

}
