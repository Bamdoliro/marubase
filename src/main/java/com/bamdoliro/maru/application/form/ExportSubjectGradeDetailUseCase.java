package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
@UseCase
public class ExportSubjectGradeDetailUseCase {

    private final FormFacade formFacade;
    private final XlsxGenerator xlsxGenerator;

    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(FormStatus.ENTERED);

        Set<String> allSubjects = extractAllSubjects(formList);

        List<String> headers = buildHeaders(allSubjects);
        List<Function<Form, Object>> columnList = buildColumnList(allSubjects);
        List<String> styleList = buildStyleList(allSubjects);

        return xlsxGenerator.executeWithDynamicHeaders(headers, formList, columnList, styleList);
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

    private List<Function<Form, Object>> buildColumnList(Set<String> allSubjects) {
        List<Function<Form, Object>> columnList = new ArrayList<>();

        columnList.add(Form::getId);
        columnList.add(Form::getExaminationNumber);
        columnList.add(form -> form.getApplicant().getName());
        columnList.add(form -> form.getApplicant().getPhoneNumber().toString());
        columnList.add(form -> form.getApplicant().getBirthday().format(DateTimeFormatter.BASIC_ISO_DATE));
        columnList.add(form -> form.getEducation().getSchool().getName());

        for (String subjectKey : allSubjects) {
            columnList.add(form -> getSubjectScore(form, subjectKey));
        }

        return columnList;
    }

    private List<String> buildStyleList(Set<String> allSubjects) {
        List<String> styleList = new ArrayList<>();

        styleList.add("default");
        styleList.add("default");
        styleList.add("default");
        styleList.add("default");
        styleList.add("default");
        styleList.add("default");

        for (String ignored : allSubjects) {
            styleList.add("right");
        }

        return styleList;
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