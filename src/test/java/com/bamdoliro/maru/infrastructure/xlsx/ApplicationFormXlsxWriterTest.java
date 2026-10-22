package com.bamdoliro.maru.infrastructure.xlsx;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Address;
import com.bamdoliro.maru.domain.form.domain.value.Parent;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationFormXlsxWriterTest {

    private static final List<String> SIDO_LIST = List.of(
            "강원특별자치도", "경기도", "경상남도", "경상북도", "대구광역시", "대전광역시",
            "부산광역시", "서울특별시", "세종특별자치시", "울산광역시", "인천광역시",
            "전남광주통합특별시", "전북특별자치도", "제주특별자치도", "충청남도", "충청북도",
            "검정고시", "기타"
    );
    private static final short HEADER_ROW_HEIGHT = 600;

    private ApplicationFormXlsxWriter writer;

    @BeforeEach
    void setUp() {
        writer = new ApplicationFormXlsxWriter(fileName -> createDummyTemplate(), new XlsxService());
    }

    @Test
    void 지원자를_8행부터_순서대로_작성한다() throws IOException {
        List<Form> formList = createFormList();

        XSSFSheet sheet = write(formList);

        DataFormatter formatter = new DataFormatter();
        for (int i = 0; i < formList.size(); i++) {
            Row row = sheet.getRow(7 + i);
            assertEquals(CellType.NUMERIC, row.getCell(0).getCellType());
            assertEquals(i + 1, (int) row.getCell(0).getNumericCellValue());
            assertEquals(formList.get(i).getApplicant().getName(), row.getCell(1).getStringCellValue());

            String birthday = row.getCell(2).getStringCellValue();
            assertEquals(CellType.STRING, row.getCell(2).getCellType());
            assertTrue(birthday.matches("\\d{8}"));
            assertTrue(SIDO_LIST.contains(formatter.formatCellValue(row.getCell(3))));
            assertTrue(SIDO_LIST.contains(formatter.formatCellValue(row.getCell(6))));
        }

        Row regular = sheet.getRow(7);
        assertEquals("20050415", regular.getCell(2).getStringCellValue());
        assertEquals("경기도", regular.getCell(3).getStringCellValue());
        assertEquals("비전중학교", regular.getCell(4).getStringCellValue());
        assertNull(regular.getCell(5));
        assertEquals("부산광역시", regular.getCell(6).getStringCellValue());
    }

    @Test
    void 검정고시_지원자는_소속지역_학교명_학년반번호를_검정고시로_작성한다() throws IOException {
        List<Form> formList = createFormList();

        XSSFSheet sheet = write(formList);

        Row row = sheet.getRow(7 + formList.size() - 1);
        assertEquals("검정고시", row.getCell(3).getStringCellValue());
        assertEquals("검정고시", row.getCell(4).getStringCellValue());
        assertEquals("검정고시", row.getCell(5).getStringCellValue());
    }

    @Test
    void 현_주소지를_시도로_변환하지_못하면_주소_원문을_작성한다() throws IOException {
        Form base = FormFixture.createForm(FormType.REGULAR);
        Form form = Form.builder()
                .applicant(base.getApplicant())
                .parent(new Parent(
                        base.getParent().getName(),
                        base.getParent().getPhoneNumber(),
                        base.getParent().getRelation(),
                        new Address("46760", "1393, Garak-daero, Gangseo-gu, Busan", "101호")
                ))
                .education(base.getEducation())
                .type(FormType.REGULAR)
                .build();

        XSSFSheet sheet = write(List.of(form));

        assertEquals("1393, Garak-daero, Gangseo-gu, Busan", sheet.getRow(7).getCell(6).getStringCellValue());
    }

    @Test
    void 양식의_시도_목록과_머리글_보호_설정을_유지한다() throws IOException {
        XSSFSheet sheet = write(createFormList());

        for (int i = 0; i < SIDO_LIST.size(); i++) {
            assertEquals(SIDO_LIST.get(i), sheet.getRow(6 + i).getCell(9).getStringCellValue());
        }
        for (int i = 0; i < 7; i++) {
            assertEquals("머리글" + (i + 1), sheet.getRow(i).getCell(0).getStringCellValue());
            assertEquals(HEADER_ROW_HEIGHT, sheet.getRow(i).getHeight());
        }
        assertEquals(3, sheet.getNumMergedRegions());
        assertTrue(sheet.getProtect());
        assertEquals(2, sheet.getDataValidations().size());
    }

    private XSSFSheet write(List<Form> formList) throws IOException {
        Resource resource = writer.write(formList);
        try (InputStream inputStream = resource.getInputStream()) {
            return new XSSFWorkbook(inputStream).getSheetAt(0);
        }
    }

    private List<Form> createFormList() {
        List<Form> formList = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> formList.add(FormFixture.createForm(FormType.REGULAR)));
        formList.add(FormFixture.createQualificationExaminationForm(FormType.REGULAR));
        return formList;
    }

    private XSSFWorkbook createDummyTemplate() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        for (int i = 0; i < 7; i++) {
            Row row = sheet.createRow(i);
            row.setHeight(HEADER_ROW_HEIGHT);
            row.createCell(0).setCellValue("머리글" + (i + 1));
        }
        sheet.addMergedRegion(CellRangeAddress.valueOf("B2:G2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B3:G3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("A4:G4"));

        for (int i = 0; i < SIDO_LIST.size(); i++) {
            Row row = sheet.getRow(6 + i) != null ? sheet.getRow(6 + i) : sheet.createRow(6 + i);
            row.createCell(9).setCellValue(SIDO_LIST.get(i));
        }
        for (int i = 0; i < 5; i++) {
            sheet.getRow(7 + i).createCell(0).setCellValue(i + 1);
        }

        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint("$J$7:$J$24");
        sheet.addValidationData(helper.createValidation(constraint, new CellRangeAddressList(7, 1048575, 3, 3)));
        sheet.addValidationData(helper.createValidation(constraint, new CellRangeAddressList(7, 1048575, 6, 6)));
        sheet.protectSheet("test");

        return workbook;
    }
}
