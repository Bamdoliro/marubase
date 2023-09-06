package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.value.Subject;
import com.bamdoliro.maru.domain.form.domain.value.SubjectMap;
import com.bamdoliro.maru.domain.form.exception.FormAlreadySubmittedException;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.pdf.GeneratePdfService;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.thymeleaf.ProcessTemplateService;
import com.bamdoliro.maru.infrastructure.thymeleaf.Templates;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.constants.Schedule;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@UseCase
public class ExportFormUseCase {

    private final FormFacade formFacade;
    private final ProcessTemplateService processTemplateService;
    private final GeneratePdfService generatePdfService;
    private final MergePdfService mergePdfService;

    public ByteArrayResource execute(User user) {
        Form form = formFacade.getForm(user);
        validateFormStatus(form);

        SubjectMap subjectMap = form.getGrade().getSubjectList().getSubjectMap();
        Map<String, Object> formMap = Map.of(
                "form", form,
                "grade21", subjectMap.getSubjectListOf(2, 1),
                "grade22", subjectMap.getSubjectListOf(2, 2),
                "grade31", subjectMap.getSubjectListOf(3, 1),
                "subjectList", getSubjectList(form),
                "year", Schedule.getAdmissionYear()
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        getRequiredTemplates(form)
                .stream()
                .map((t) -> processTemplateService.execute(t, formMap))
                .map(generatePdfService::execute)
                .forEach((s) -> mergePdfService.execute(pdfMerger, s));

        mergedDocument.close();
        pdfMerger.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private List<SubjectVO> getSubjectList(Form form) {
        List<SubjectVO> value = new ArrayList<>();
        Map<String, List<Subject>> subjectMap = form.getGrade()
                .getSubjectListValue()
                .stream()
                .collect(Collectors.groupingBy(Subject::getSubjectName));

        subjectMap.forEach((key, values) -> {
            SubjectVO subject = new SubjectVO(key);
            values.forEach(v -> {
                if (Objects.nonNull(v.getOriginalScore())) {
                    subject.score = v.getOriginalScore();
                } else {
                    try {
                        SubjectVO.class.getField("achievementLevel" + v.toString())
                                .set(subject, v.getAchievementLevel());
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            value.add(subject);
        });

        return value;
    }

    private void validateFormStatus(Form form) {
        if (!(form.isSubmitted() || form.isRejected())) {
            throw new FormAlreadySubmittedException();
        }
    }

    private List<String> getRequiredTemplates(Form form) {
        if (form.getType().isRegular()) {
            return List.of(
                    Templates.FORM,
                    Templates.GRADE_TABLE,
                    Templates.DOCUMENT,
                    Templates.NO_SMOKING
            );
        }

        return List.of(
                Templates.FORM,
                Templates.GRADE_TABLE,
                Templates.DOCUMENT,
                Templates.RECOMMENDATION,
                Templates.NO_SMOKING
        );
    }
}

@Getter
class SubjectVO {

    private String subjectName;

    public AchievementLevel achievementLevel21;

    public AchievementLevel achievementLevel22;

    public AchievementLevel achievementLevel31;

    Integer score;

    public SubjectVO(String subjectName) {
        this.subjectName = subjectName;
    }
}