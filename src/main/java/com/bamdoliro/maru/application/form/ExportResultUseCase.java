package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.value.SubjectMap;
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
        List<Form> formList = formFacade.getSortedFormList(null);
        Workbook workbook = xlsxService.openTemplate("전체결과");
        Sheet sheet = workbook.getSheetAt(0);

        CellStyle defaultCellStyle = xlsxService.createDefaultCellStyle(workbook);
        CellStyle rightCellStyle = xlsxService.createRightCellStyle(workbook);
        CellStyle emptyCellStyle = xlsxService.createEmptyCellStyle(workbook);


        for (int index = 0; index < formList.size(); index++) {
            Form form = formList.get(index);
            Row row = sheet.createRow(index + XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE);
            SubjectMap subjectMap = form.getGrade().getSubjectList().getSubjectMap();

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

            Cell subjectCount21 = row.createCell(11);
            subjectCount21.setCellValue(subjectMap.getSubjectListOf(2,1).size());
            subjectCount21.setCellStyle(defaultCellStyle);

            Cell subjectTotalScore21 = row.createCell(12);
            subjectTotalScore21.setCellValue(subjectMap.getSubjectListOf(2,1).totalScore());
            subjectTotalScore21.setCellStyle(defaultCellStyle);

            Cell subjectCount22 = row.createCell(13);
            subjectCount22.setCellValue(subjectMap.getSubjectListOf(2,2).size());
            subjectCount22.setCellStyle(defaultCellStyle);

            Cell subjectTotalScore22 = row.createCell(14);
            subjectTotalScore22.setCellValue(subjectMap.getSubjectListOf(2,2).totalScore());
            subjectTotalScore22.setCellStyle(defaultCellStyle);

            Cell subjectCount31 = row.createCell(15);
            subjectCount31.setCellValue(subjectMap.getSubjectListOf(3,1).size());
            subjectCount31.setCellStyle(defaultCellStyle);

            Cell subjectTotalScore31 = row.createCell(16);
            subjectTotalScore31.setCellValue(subjectMap.getSubjectListOf(3,1).totalScore());
            subjectTotalScore31.setCellStyle(defaultCellStyle);

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
            Cell ncsScoreCell = row.createCell(22);
            Cell codingTestScoreCell = row.createCell(23);
            Cell totalScoreCell = row.createCell(24);
            totalScoreCell.setCellStyle(rightCellStyle);

            if (form.tookSecondRound()) {
                depthInterviewScoreCell.setCellValue(form.getScore().getDepthInterviewScore());
                depthInterviewScoreCell.setCellStyle(rightCellStyle);

                ncsScoreCell.setCellValue(form.getScore().getNcsScore());
                ncsScoreCell.setCellStyle(rightCellStyle);

                if (form.getType().isMeister()) {
                    codingTestScoreCell.setCellValue(form.getScore().getCodingTestScore());
                    codingTestScoreCell.setCellStyle(rightCellStyle);
                } else {
                    codingTestScoreCell.setCellStyle(emptyCellStyle);
                }

                totalScoreCell.setCellValue(form.getScore().getTotalScore());
            } else {
                depthInterviewScoreCell.setCellStyle(emptyCellStyle);
                ncsScoreCell.setCellStyle(emptyCellStyle);
                codingTestScoreCell.setCellStyle(emptyCellStyle);

                totalScoreCell.setCellValue(form.getScore().getFirstRoundScore());
            }
        }

        return xlsxService.convertToByteArrayResource(workbook);
    }
}
