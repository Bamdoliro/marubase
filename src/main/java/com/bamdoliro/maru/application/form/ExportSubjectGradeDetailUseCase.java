package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.FormColumn;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@UseCase
public class ExportSubjectGradeDetailUseCase {

    private final FormFacade formFacade;
    private final XlsxGenerator xlsxGenerator;

    @Transactional(readOnly = true)
    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(FormStatus.PASSED);

        Set<String> allSubjects = extractAllSubjects(formList);

        List<String> headers = buildHeaders(allSubjects);
        List<FormColumn> columnList = buildColumnList(allSubjects);

        return xlsxGenerator.executeWithDynamicHeaders(headers, formList, columnList);
    }

    private Set<String> extractAllSubjects(List<Form> formList) {
        Set<String> allSubjects = new LinkedHashSet<>();

        for (Form form : formList) {
            List<Subject> subjects = form.getGrade().getSubjectListValue();
            for (Subject subject : subjects) {
                String subjectKey = String.format("%d%d_%s",
                    subject.getGrade(),
                    subject.getSemester(),
                    subject.getSubjectName());
                allSubjects.add(subjectKey);
            }
        }

        return allSubjects;
    }

    private List<String> buildHeaders(Set<String> allSubjects) {
        List<String> headers = new ArrayList<>();

        headers.add("ID");
        headers.add("수험번호");
        headers.add("이름");
        headers.add("전화번호");
        headers.add("생년월일");
        headers.add("학교명");

        for (String subjectKey : allSubjects) {
            String[] parts = subjectKey.split("_");
            if (parts.length == 2) {
                String gradeSemester = parts[0];
                String subjectName = parts[1];
                headers.add(String.format("%s학년 %s학기 %s",
                    gradeSemester.substring(0, 1),
                    gradeSemester.substring(1, 2),
                    subjectName));
            }
        }

        return headers;
    }

    private List<FormColumn> buildColumnList(Set<String> allSubjects) {
        List<FormColumn> columnList = new ArrayList<>();

        columnList.add(FormColumn.text(Form::getId));
        columnList.add(FormColumn.text(Form::getExaminationNumber));
        columnList.add(FormColumn.text(form -> form.getApplicant().getName()));
        columnList.add(FormColumn.text(form -> form.getApplicant().getPhoneNumber().toString()));
        columnList.add(FormColumn.text(form -> form.getApplicant().getBirthday().format(DateTimeFormatter.BASIC_ISO_DATE)));
        columnList.add(FormColumn.text(form -> form.getEducation().getSchool().getName()));

        for (String subjectKey : allSubjects) {
            columnList.add(FormColumn.text(form -> getSubjectScore(form, subjectKey)));
        }

        return columnList;
    }

    private Object getSubjectScore(Form form, String subjectKey) {
        String[] parts = subjectKey.split("_");
        if (parts.length != 2) return "";

        String gradeSemester = parts[0];
        String subjectName = parts[1];

        if (gradeSemester.length() != 2) return "";

        int grade = Integer.parseInt(gradeSemester.substring(0, 1));
        int semester = Integer.parseInt(gradeSemester.substring(1, 2));

        List<Subject> subjects = form.getGrade().getSubjectListValue();
        for (Subject subject : subjects) {
            if (subject.getGrade().equals(grade) &&
                subject.getSemester().equals(semester) &&
                subject.getSubjectName().equals(subjectName)) {
                return subject.getAchievementLevel().getDescription();
            }
        }

        return "";
    }
}