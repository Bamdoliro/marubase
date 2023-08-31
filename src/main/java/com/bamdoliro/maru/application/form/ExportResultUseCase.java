package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxService;
import com.bamdoliro.maru.infrastructure.xlsx.constant.XlsxConstant;
import com.bamdoliro.maru.shared.annotation.UseCase;
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
public class ExportResultUseCase {

    private final FormFacade formFacade;
    private final XlsxService xlsxService;

    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(FormStatus.PASSED);
        Workbook workbook = xlsxService.openTemplate("최종합격자");
        Sheet sheet = workbook.getSheetAt(0);

        CellStyle defaultCellStyle = xlsxService.createDefaultCellStyle(workbook);
        CellStyle rightCellStyle = xlsxService.createRightCellStyle(workbook);
        CellStyle emptyCellStyle = xlsxService.createEmptyCellStyle(workbook);
        CellStyle dateCellStyle = xlsxService.createDateCellStyle(workbook);

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

            Cell phoneNumberCell = row.createCell(6);
            phoneNumberCell.setCellValue(form.getApplicant().getPhoneNumber().toString());
            phoneNumberCell.setCellStyle(defaultCellStyle);

            Cell genderCell = row.createCell(7);
            genderCell.setCellValue(form.getApplicant().getGender().getDescription());
            genderCell.setCellStyle(defaultCellStyle);

            Cell birthdayCell = row.createCell(8);
            birthdayCell.setCellValue(form.getApplicant().getBirthday());
            birthdayCell.setCellStyle(dateCellStyle);

            Cell locationCell = row.createCell(9);
            locationCell.setCellValue(form.getEducation().getSchool().getLocation());
            locationCell.setCellStyle(defaultCellStyle);

            Cell graduationCell = row.createCell(10);
            graduationCell.setCellValue(form.getEducation().getGraduationTypeToString());
            graduationCell.setCellStyle(defaultCellStyle);

            Cell schoolCell = row.createCell(11);
            schoolCell.setCellValue(form.getEducation().getSchool().getName());
            schoolCell.setCellStyle(defaultCellStyle);

            Cell schoolCodeCell = row.createCell(12);
            schoolCodeCell.setCellValue(Integer.parseInt(form.getEducation().getSchool().getCode()));
            schoolCodeCell.setCellStyle(defaultCellStyle);

            Cell parentNameCell = row.createCell(13);
            parentNameCell.setCellValue(form.getParent().getName());
            parentNameCell.setCellStyle(defaultCellStyle);

            Cell parentPhoneNubmerCell = row.createCell(14);
            parentPhoneNubmerCell.setCellValue(form.getParent().getPhoneNumber().toString());
            parentPhoneNubmerCell.setCellStyle(defaultCellStyle);

            Cell addressCell = row.createCell(15);
            addressCell.setCellValue(form.getParent().getAddress().toString());
            addressCell.setCellStyle(defaultCellStyle);

            Cell parentRelationCell = row.createCell(16);
            parentRelationCell.setCellValue(form.getParent().getRelation());
            parentRelationCell.setCellStyle(defaultCellStyle);

            Cell subjectGradeScoreCell = row.createCell(17);
            subjectGradeScoreCell.setCellValue(form.getScore().getSubjectGradeScore());
            subjectGradeScoreCell.setCellStyle(rightCellStyle);

            Cell attendanceScoreCell = row.createCell(18);
            attendanceScoreCell.setCellValue(form.getScore().getAttendanceScore());
            attendanceScoreCell.setCellStyle(rightCellStyle);

            Cell volunteerScoreCell = row.createCell(19);
            volunteerScoreCell.setCellValue(form.getScore().getVolunteerScore());
            volunteerScoreCell.setCellStyle(rightCellStyle);

            Cell bonusScoreCell = row.createCell(20);
            bonusScoreCell.setCellValue(form.getScore().getBonusScore());
            bonusScoreCell.setCellStyle(rightCellStyle);

            Cell depthInterviewScoreCell = row.createCell(21);
            depthInterviewScoreCell.setCellValue(form.getScore().getDepthInterviewScore());
            depthInterviewScoreCell.setCellStyle(rightCellStyle);

            Cell ncsScoreCell = row.createCell(22);
            ncsScoreCell.setCellValue(form.getScore().getNcsScore());
            ncsScoreCell.setCellStyle(rightCellStyle);

            Cell codingTestScoreCell = row.createCell(23);
            if (form.getType().isMeister()) {
                codingTestScoreCell.setCellValue(form.getScore().getCodingTestScore());
                codingTestScoreCell.setCellStyle(rightCellStyle);
            } else {
                codingTestScoreCell.setCellStyle(emptyCellStyle);
            }

            Cell totalScoreCell = row.createCell(24);
            totalScoreCell.setCellValue(form.getScore().getTotalScore());
            totalScoreCell.setCellStyle(rightCellStyle);
        }

        return xlsxService.convertToByteArrayResource(workbook);
    }
}
