package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.FormColumn;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class ExportSecondRoundResultUseCase {

    private final FormRepository formRepository;
    private final FormFacade formFacade;
    private final XlsxGenerator xlsxGenerator;

    public Resource execute() throws IOException {
        List<Form> formList = formRepository.findSecondRoundForm()
                .stream()
                .sorted(
                        formFacade.getFormComparator()
                                .thenComparing(form -> form.getScore().getTotalScore())
                )
                .toList();

        List<FormColumn> columnList = List.of(
                FormColumn.text(Form::getId),
                FormColumn.text(Form::getExaminationNumber),
                FormColumn.text(form -> form.getOriginalType().getDescription()),
                FormColumn.text(form -> form.getType().getDescription()),
                FormColumn.text(form -> form.getStatus().getDescription()),
                FormColumn.text(form -> form.getApplicant().getName()),
                FormColumn.text((form -> form.getApplicant().getGender().getDescription())),
                FormColumn.text(form -> form.getApplicant().getBirthday()),
                FormColumn.text(form -> form.getEducation().getSchool().getLocation()),
                FormColumn.text(form -> form.getEducation().getGraduationTypeToString()),
                FormColumn.text(form -> form.getEducation().getSchool().getName()),
                FormColumn.text(form -> form.getEducation().getSchool().getCode()),
                FormColumn.score(form -> MathUtil.roundTo(form.getScore().getSubjectGradeScore(), 3)),
                FormColumn.score(form -> form.getScore().getAttendanceScore()),
                FormColumn.score(form -> form.getScore().getVolunteerScore()),
                FormColumn.score(form -> form.getScore().getBonusScore()),
                FormColumn.score(form -> form.getScore().getDepthInterviewScore()),
                FormColumn.score(form -> form.getScore().getNcsScore()),
                FormColumn.score(form -> form.getScore().getCodingTestScore()),
                FormColumn.score(form -> MathUtil.roundTo(form.getScore().getTotalScore(), 3))
        );

        return xlsxGenerator.execute("2차전형결과", formList, columnList);
    }
}
