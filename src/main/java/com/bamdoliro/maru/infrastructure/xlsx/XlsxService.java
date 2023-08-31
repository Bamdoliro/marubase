package com.bamdoliro.maru.infrastructure.xlsx;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class XlsxService {

    public Workbook openTemplate(String path) throws IOException {
        InputStream inputStream = new ClassPathResource("templates/xlsx/" + path + ".xlsx").getInputStream();
        return new XSSFWorkbook(inputStream);
    }

    public CellStyle createDefaultCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(style);
        return style;
    }

    public CellStyle createRightCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(style);
        return style;
    }

    public CellStyle createDateCellStyle(Workbook workbook) {
        CreationHelper createHelper = workbook.getCreationHelper();
        short format = createHelper.createDataFormat().getFormat("yyyy년 mm월 dd일");
        CellStyle style = createDefaultCellStyle(workbook);
        style.setDataFormat(format);
        return style;
    }

    public CellStyle createEmptyCellStyle(Workbook workbook) {
        CellStyle style = createDefaultCellStyle(workbook);
        setBackgroundColor(style, "F3F3F3");
        return style;
    }

    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }

    private void setBackgroundColor(CellStyle style, String hex) {
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(new XSSFColor(hexToColor(hex), new DefaultIndexedColorMap()));
    }

    private Color hexToColor(String hex) {
        return Color.decode("#" + hex);
    }

    public ByteArrayResource convertToByteArrayResource(Workbook workbook) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    public void writeTitle(Sheet sheet, String title) {
        Cell titleCell = sheet.getRow(0).getCell(0);
        titleCell.setCellValue(title);
    }
}
