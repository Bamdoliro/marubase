package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxService;
import com.bamdoliro.maru.infrastructure.xlsx.constant.XlsxConstant;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.bamdoliro.maru.shared.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class ExportFirstRoundResultUseCase {

    private final FormRepository formRepository;
    private final FormFacade formFacade;
    private final XlsxService xlsxService;

    public Resource execute() throws IOException {
        List<Form> formList = formRepository.findFirstRoundForm()
                .stream()
                .sorted(
                        formFacade.getFormComparator()
                                .thenComparing(form -> form.getScore().getFirstRoundScore())
                )
                .toList();

        Workbook workbook = xlsxService.openTemplate("1차전형결과");
        Sheet sheet = workbook.getSheetAt(0);

        CellStyle defaultCellStyle = xlsxService.createDefaultCellStyle(workbook);
        CellStyle rightCellStyle = xlsxService.createRightCellStyle(workbook);

        for (int index = 0; index < formList.size(); index++) {
            Form form = formList.get(index);
            Row row = sheet.createRow(index + XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE);

            Cell idCell = row.createCell(0);
            idCell.setCellValue(form.getId());
            idCell.setCellStyle(defaultCellStyle);

            Cell examinationNumberCell = row.createCell(1);
            examinationNumberCell.setCellValue(form.getExaminationNumber());
            examinationNumberCell.setCellStyle(defaultCellStyle);

            Cell originalTypeCell = row.createCell(2);
            originalTypeCell.setCellValue(form.getOriginalType().getDescription());
            originalTypeCell.setCellStyle(defaultCellStyle);

            Cell typeCell = row.createCell(3);
            typeCell.setCellValue(form.getType().getDescription());
            typeCell.setCellStyle(defaultCellStyle);

            Cell resultCell = row.createCell(4);
            resultCell.setCellValue(form.getStatus().getDescription());
            resultCell.setCellStyle(defaultCellStyle);

            Cell nameCell = row.createCell(5);
            nameCell.setCellValue(form.getApplicant().getName());
            nameCell.setCellStyle(defaultCellStyle);

            Cell genderCell = row.createCell(6);
            genderCell.setCellValue(form.getApplicant().getGender().getDescription());
            genderCell.setCellStyle(defaultCellStyle);

            Cell locationCell = row.createCell(7);
            locationCell.setCellValue(form.getEducation().getSchool().getLocation());
            locationCell.setCellStyle(defaultCellStyle);

            Cell graduationCell = row.createCell(8);
            graduationCell.setCellValue(form.getEducation().getGraduationTypeToString());
            graduationCell.setCellStyle(defaultCellStyle);

            Cell schoolCell = row.createCell(9);
            schoolCell.setCellValue(form.getEducation().getSchool().getName());
            schoolCell.setCellStyle(defaultCellStyle);

            Cell schoolCodeCell = row.createCell(10);
            schoolCodeCell.setCellValue(form.getEducation().getSchool().getCode());
            schoolCodeCell.setCellStyle(defaultCellStyle);

            Cell subjectGradeScoreCell = row.createCell(11);
            subjectGradeScoreCell.setCellValue(MathUtil.roundTo(form.getScore().getSubjectGradeScore(), 3));
            subjectGradeScoreCell.setCellStyle(rightCellStyle);

            Cell attendanceScoreCell = row.createCell(12);
            attendanceScoreCell.setCellValue(form.getScore().getAttendanceScore());
            attendanceScoreCell.setCellStyle(rightCellStyle);

            Cell volunteerScoreCell = row.createCell(13);
            volunteerScoreCell.setCellValue(form.getScore().getVolunteerScore());
            volunteerScoreCell.setCellStyle(rightCellStyle);

            Cell bonusScoreCell = row.createCell(14);
            bonusScoreCell.setCellValue(form.getScore().getBonusScore());
            bonusScoreCell.setCellStyle(rightCellStyle);

            Cell totalScoreCell = row.createCell(15);
            totalScoreCell.setCellValue(MathUtil.roundTo(form.getScore().getFirstRoundScore(), 3));
            totalScoreCell.setCellStyle(rightCellStyle);
        }

        return xlsxService.convertToByteArrayResource(workbook);
    }
}
