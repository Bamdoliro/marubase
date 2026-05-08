package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.FormColumn;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class ExportFinalPassedFormUseCase {

    private final FormFacade formFacade;
    private final XlsxGenerator xlsxGenerator;

    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(FormStatus.ENTERED);

        List<FormColumn> columns = List.of(
                FormColumn.text(Form::getId),
                FormColumn.text(Form::getExaminationNumber),
                FormColumn.text(form -> form.getOriginalType().getDescription()),
                FormColumn.text(form -> form.getType().getDescription()),
                FormColumn.text(form -> form.getStatus().getDescription()),
                FormColumn.text(form -> form.getApplicant().getName()),
                FormColumn.text(form -> form.getApplicant().getPhoneNumber().toString()),
                FormColumn.text(form -> form.getApplicant().getGender().getDescription()),
                FormColumn.text(form -> form.getApplicant().getBirthday().format(DateTimeFormatter.BASIC_ISO_DATE)),
                FormColumn.text(form -> form.getEducation().getSchool().getLocation()),
                FormColumn.text(form -> form.getEducation().getGraduationTypeToString()),
                FormColumn.text(form -> form.getEducation().getSchool().getName()),
                FormColumn.text(form -> Integer.parseInt(form.getEducation().getSchool().getCode())),
                FormColumn.text(form -> form.getParent().getName()),
                FormColumn.text(form -> form.getParent().getPhoneNumber().toString()),
                FormColumn.text(form -> form.getParent().getAddress().toString()),
                FormColumn.text(form -> form.getParent().getRelation()),
                FormColumn.score(form -> MathUtil.roundTo(form.getScore().getSubjectGradeScore(), 3)),
                FormColumn.score(form -> form.getScore().getAttendanceScore()),
                FormColumn.score(form -> form.getScore().getVolunteerScore()),
                FormColumn.score(form -> form.getScore().getBonusScore()),
                FormColumn.score(form -> form.getScore().getDepthInterviewScore()),
                FormColumn.score(form -> form.getScore().getNcsScore()),
                FormColumn.score(form -> form.getScore().getCodingTestScore()),
                FormColumn.score(form -> MathUtil.roundTo(form.getScore().getTotalScore(), 3))
        );
        return xlsxGenerator.execute("최종합격자", formList, columns);
    }
}
