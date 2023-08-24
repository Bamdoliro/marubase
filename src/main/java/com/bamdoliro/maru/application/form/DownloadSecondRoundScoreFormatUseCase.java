package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxService;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.bamdoliro.maru.shared.constants.XlsxConstant.DEFAULT_FIRST_ROW_INDEX;

@Slf4j
@RequiredArgsConstructor
@UseCase
public class DownloadSecondRoundScoreFormatUseCase {

    private final FormRepository formRepository;
    private final XlsxService xlsxService;

    public Resource execute() throws IOException {
        List<Form> formList = formRepository.findByStatus(FormStatus.FIRST_PASSED)
                .stream()
                .sorted(Comparator
                        .comparing(this::getOrder)
                        .thenComparing(Form::getChangedToRegular)
                        .thenComparing(Form::getExaminationNumber))
                .toList();

        Workbook workbook = xlsxService.openTemplate("2차전형점수양식.xlsx");
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = xlsxService.createDefaultCellStyle(workbook);
        sheet.setColumnHidden(6, true);

        for (int index = 0; index < formList.size(); index++) {
            Form form = formList.get(index);
            Row row = sheet.createRow(index + DEFAULT_FIRST_ROW_INDEX);
            int xlsxIndex = index + DEFAULT_FIRST_ROW_INDEX + 1;

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
            showCell.setCellFormula(String.format("IF(C%1$d=\"마이스터인재전형\", IF(OR(ISBLANK(D%1$d), ISBLANK(E%1$d), ISBLANK(F%1$d)), FALSE, TRUE), IF(OR(ISBLANK(D%1$d), ISBLANK(E%1$d)), FALSE, TRUE))", xlsxIndex));
        }

        return xlsxService.convertToByteArrayResource(workbook);
    }

    private Integer getOrder(Form form) {
        log.error(form.getType().toString());
        return getOrderMap().get(form.getType().getCategory());
    }

    private Map<FormType.Category, Integer> getOrderMap() {
        return Map.of(
                FormType.Category.REGULAR, 1,
                FormType.Category.MEISTER_TALENT, 2,
                FormType.Category.SOCIAL_INTEGRATION, 3,
                FormType.Category.SUPERNUMERARY, 4
        );
    }
}
