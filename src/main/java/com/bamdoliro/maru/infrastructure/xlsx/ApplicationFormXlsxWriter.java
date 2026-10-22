package com.bamdoliro.maru.infrastructure.xlsx;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.infrastructure.xlsx.loader.XlsxTemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 고입업무지원시스템 지원자등록양식의 8행부터 지원자 정보를 채운다.
 * 양식의 보호된 부분과 학년반번호는 채우지 않음. 또한 현재 소재지의 경우 부모님의 주소를 적되 그 광역자치단체가 명확하게 드러나지 않는 것은 원문으로 대체함.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ApplicationFormXlsxWriter {

    public static final String TEMPLATE_FILE_NAME = "ApplicationForm2026.xlsx";
    private static final int FIRST_DATA_ROW_INDEX = 7;
    private static final String QUALIFICATION_EXAMINATION = "검정고시";

    private final XlsxTemplateLoader xlsxTemplateLoader;
    private final XlsxService xlsxService;

    public Resource write(List<Form> formList) throws IOException {
        Workbook workbook = xlsxTemplateLoader.open(TEMPLATE_FILE_NAME);
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle style = createTextCellStyle(workbook);

        for (int index = 0; index < formList.size(); index++) {
            Form form = formList.get(index);
            Row row = getOrCreateRow(sheet, FIRST_DATA_ROW_INDEX + index);

            setCell(row, 0, index + 1, style);
            setCell(row, 1, form.getApplicant().getName(), style);
            setCell(row, 2, form.getApplicant().getBirthday().format(DateTimeFormatter.BASIC_ISO_DATE), style);
            setCell(row, 3, getSchoolSido(form), style);
            setCell(row, 4, getSchoolName(form), style);
            setCell(row, 5, getClassInfo(form), style);
            setCell(row, 6, getHomeSido(form), style);
        }

        return xlsxService.convertToByteArrayResource(workbook);
    }

    private CellStyle createTextCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("@"));
        style.setLocked(false);
        return style;
    }

    private Row getOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        return row != null ? row : sheet.createRow(rowIndex);
    }

    private void setCell(Row row, int columnIndex, Object value, CellStyle style) {
        if (value == null) {
            return;
        }

        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }

        if (value instanceof Integer number) {
            cell.setCellValue(number);
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    private String getSchoolSido(Form form) {
        if (form.getEducation().isQualificationExamination()) {
            return QUALIFICATION_EXAMINATION;
        }

        return SidoNormalizer.normalize(form.getEducation().getSchool().getLocation())
                .orElseGet(() -> {
                    log.warn("중학교 소속지역을 시·도로 변환하지 못했습니다. formId={}", form.getId());
                    return null;
                });
    }

    private String getSchoolName(Form form) {
        if (form.getEducation().isQualificationExamination()) {
            return QUALIFICATION_EXAMINATION;
        }

        return form.getEducation().getSchool().getName();
    }

    private String getClassInfo(Form form) {
        if (form.getEducation().isQualificationExamination()) {
            return QUALIFICATION_EXAMINATION;
        }

        return null;
    }

    // 보호자 주소 기준, 시·도로 변환하지 못하면 주소 원문을 쓴다.
    private String getHomeSido(Form form) {
        String address = form.getParent().getAddress().getAddress();
        return SidoNormalizer.normalize(address)
                .orElseGet(() -> {
                    log.warn("현 주소지를 시·도로 변환하지 못해 주소 원문을 기입합니다. formId={}", form.getId());
                    return address;
                });
    }
}
