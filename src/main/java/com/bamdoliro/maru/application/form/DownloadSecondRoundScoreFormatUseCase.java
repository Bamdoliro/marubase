package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxService;
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

import static com.bamdoliro.maru.infrastructure.xlsx.constant.XlsxConstant.FIRST_ROW_INDEX_WITH_NO_TITLE;

@RequiredArgsConstructor
@UseCase
public class DownloadSecondRoundScoreFormatUseCase {

    private final FormFacade formFacade;
    private final XlsxService xlsxService;

    public Resource execute() throws IOException {
        List<Form> formList = formFacade.getSortedFormList(FormStatus.FIRST_PASSED);

        Workbook workbook = xlsxService.openTemplate("2차전형점수양식");
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = xlsxService.createDefaultCellStyle(workbook);
        sheet.setColumnHidden(6, true);

        for (int index = 0; index < formList.size(); index++) {
            Form form = formList.get(index);
            Row row = sheet.createRow(index + FIRST_ROW_INDEX_WITH_NO_TITLE);
            int xlsxIndex = index + FIRST_ROW_INDEX_WITH_NO_TITLE + 1;

            Cell examinationNumberCell = row.createCell(0);
            examinationNumberCell.setCellValue(form.getExaminationNumber());
            examinationNumberCell.setCellStyle(cellStyle);

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(form.getApplicant().getName());
            nameCell.setCellStyle(cellStyle);

            Cell formTypeCell = row.createCell(2);
            formTypeCell.setCellValue(form.getType().getCategory().getDescription());
            formTypeCell.setCellStyle(cellStyle);

            Cell depthInterviewCell = row.createCell(3);
            depthInterviewCell.setCellStyle(cellStyle);

            Cell ncsCell = row.createCell(4);
            ncsCell.setCellStyle(cellStyle);

            Cell codingTestCell = row.createCell(5);
            codingTestCell.setCellStyle(cellStyle);

            Cell showCell = row.createCell(6);
            showCell.setCellStyle(cellStyle);
            showCell.setCellFormula(String.format("IF(C%1$d=\"마이스터인재전형\", IF(OR(D%1$d=\"불참\", E%1$d=\"불참\", F%1$d=\"불참\"), FALSE, TRUE), IF(OR(D%1$d=\"불참\", E%1$d=\"불참\"), FALSE, TRUE))", xlsxIndex));

        }

        return xlsxService.convertToByteArrayResource(workbook);
    }
}
