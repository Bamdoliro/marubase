package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import com.bamdoliro.maru.domain.fair.domain.Fair;
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
import java.time.format.DateTimeFormatter;

import static com.bamdoliro.maru.infrastructure.xlsx.constant.XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE;

@RequiredArgsConstructor
@UseCase
public class ExportAttendeeListUseCase {

    private final FairFacade fairFacade;
    private final XlsxService xlsxService;

    public Resource execute(Long fairId) throws IOException {
        Fair fair = fairFacade.getFairDetail(fairId);
        Workbook workbook = xlsxService.openTemplate("입학설명회참가자명단");
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = xlsxService.createDefaultCellStyle(workbook);

        String title = fair.getStart().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + " 입학설명회 신청자 명단";
        xlsxService.writeTitle(sheet, title);

        for (int index = 0; index < fair.getAttendeeList().size(); index++) {
            Attendee attendee = fair.getAttendeeList().get(index);
            Row row = sheet.createRow(index + FIRST_ROW_INDEX_WITH_TITLE);

            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(attendee.getName());
            nameCell.setCellStyle(cellStyle);

            Cell phoneNumberCell = row.createCell(1);
            phoneNumberCell.setCellValue(attendee.getPhoneNumber());
            phoneNumberCell.setCellStyle(cellStyle);

            Cell headcountCell = row.createCell(2);
            headcountCell.setCellValue(attendee.getHeadcount());
            headcountCell.setCellStyle(cellStyle);

            Cell schoolCell = row.createCell(3);
            schoolCell.setCellValue(attendee.getSchoolName());
            schoolCell.setCellStyle(cellStyle);

            Cell typeCell = row.createCell(4);
            typeCell.setCellValue(attendee.getType());
            typeCell.setCellStyle(cellStyle);

            Cell questionCell = row.createCell(5);
            questionCell.setCellValue(attendee.getQuestion());
            questionCell.setCellStyle(cellStyle);
        }

        return xlsxService.convertToByteArrayResource(workbook);
    }
}
