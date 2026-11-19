package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.service.FormFacade;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxGenerator;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxService;
import com.bamdoliro.maru.infrastructure.xlsx.constant.XlsxConstant;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExportFormXlsxRegressionTest {

    @Mock
    private FormRepository formRepository;

    private ExportFinalPassedFormUseCase exportFinalPassedFormUseCase;
    private ExportSecondRoundResultUseCase exportSecondRoundResultUseCase;

    @BeforeEach
    void setUp() {
        FormFacade formFacade = new FormFacade(formRepository);
        XlsxGenerator generator = new XlsxGenerator(new XlsxService());
        exportFinalPassedFormUseCase = new ExportFinalPassedFormUseCase(formFacade, formRepository, generator);
        exportSecondRoundResultUseCase = new ExportSecondRoundResultUseCase(formRepository, formFacade, generator);
    }

    @Test
    void 이차전형_생년월일은_일련번호가_아닌_날짜_문자열로_내보낸다() throws Exception {
        given(formRepository.findSecondRoundForm()).willReturn(List.of(form(1001L, FormStatus.PASSED)));

        try (Workbook workbook = WorkbookFactory.create(exportSecondRoundResultUseCase.execute().getInputStream())) {
            Cell birthday = workbook.getSheetAt(0)
                    .getRow(XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE).getCell(7);
            assertThat(birthday.getCellType()).isEqualTo(CellType.STRING);
            assertThat(new DataFormatter().formatCellValue(birthday)).isEqualTo("20050415");
        }
    }

    @Test
    void 입학_처리_전에도_최종합격자를_내보낸다() throws Exception {
        given(formRepository.findByStatusIn(Set.of(FormStatus.PASSED, FormStatus.ENTERED)))
                .willReturn(List.of(form(1001L, FormStatus.PASSED)));

        try (Workbook workbook = WorkbookFactory.create(exportFinalPassedFormUseCase.execute().getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet.getRow(XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE).getCell(1).getNumericCellValue())
                    .isEqualTo(1001);
            assertThat(sheet.getRow(XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE).getCell(4).getStringCellValue())
                    .isEqualTo(FormStatus.PASSED.getDescription());
        }
    }

    @Test
    void 최종합격과_입학만_포함하고_기존_수험번호_정렬을_유지한다() throws Exception {
        List<Form> forms = List.of(
                form(1003L, FormStatus.ENTERED),
                form(1002L, FormStatus.FAILED),
                form(1001L, FormStatus.PASSED),
                form(1004L, FormStatus.FIRST_PASSED),
                form(1005L, FormStatus.APPROVED)
        );
        given(formRepository.findByStatusIn(anyCollection())).willAnswer(invocation -> {
            Collection<FormStatus> statuses = invocation.getArgument(0);
            return forms.stream().filter(form -> statuses.contains(form.getStatus())).toList();
        });

        try (Workbook workbook = WorkbookFactory.create(exportFinalPassedFormUseCase.execute().getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int start = XlsxConstant.FIRST_ROW_INDEX_WITH_TITLE;
            assertThat(sheet.getLastRowNum()).isEqualTo(start + 1);
            assertThat(sheet.getRow(start).getCell(1).getNumericCellValue()).isEqualTo(1001);
            assertThat(sheet.getRow(start + 1).getCell(1).getNumericCellValue()).isEqualTo(1003);
        }
        verify(formRepository).findByStatusIn(Set.of(FormStatus.PASSED, FormStatus.ENTERED));
    }

    private Form form(Long examinationNumber, FormStatus status) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "id", examinationNumber);
        ReflectionTestUtils.setField(form, "examinationNumber", examinationNumber);
        ReflectionTestUtils.setField(form, "status", status);
        form.getScore().updateSecondRoundScore(50.0, 20.0);
        return form;
    }
}
