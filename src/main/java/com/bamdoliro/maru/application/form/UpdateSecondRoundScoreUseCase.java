package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.InvalidFileException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.xlsx.XlsxService;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@UseCase
public class UpdateSecondRoundScoreUseCase {

    private final FormRepository formRepository;
    private final XlsxService xlsxService;

    private CellStyle errorCellStyle;

    @Transactional
    public Resource execute(MultipartFile xlsx) throws IOException {
        Workbook workbook = new XSSFWorkbook(xlsx.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        errorCellStyle = xlsxService.createDefaultCellStyle(workbook);
        errorCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        errorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<Form> formList = formRepository.findByStatus(FormStatus.FIRST_PASSED);
        formList.sort(Comparator.comparing(Form::getExaminationNumber));
        List<SecondScoreVo> secondScoreVoList = getSecondScoreVoList(sheet);
        if (secondScoreVoList == null) {
            return xlsxService.convertToByteArrayResource(workbook);
        }
        validateList(formList, secondScoreVoList);

        for (int index = 0; index < formList.size(); index++) {
            Form form = formList.get(index);
            SecondScoreVo secondScoreVo = secondScoreVoList.get(index);
            validate(form, secondScoreVo);

            updateFormOrNoShow(form, secondScoreVo);
        }
        workbook.close();

        return null;
    }

    private List<SecondScoreVo> getSecondScoreVoList(Sheet sheet) {
        List<SecondScoreVo> voList = IntStream.range(1, sheet.getPhysicalNumberOfRows())
                .mapToObj(sheet::getRow)
                .map(this::getSecondScoreFrom)
                .collect(Collectors.toList());

        if (voList.contains(null)) {
            return null;
        }

        voList.sort(Comparator.comparingLong(SecondScoreVo::getExaminationNumber));

        return voList;
    }

    private SecondScoreVo getSecondScoreFrom(Row row) {
        List<Cell> invalidCellTypeList = validateCellType(row);
        boolean isValidScore = validateScore(row, invalidCellTypeList);

        if (invalidCellTypeList.isEmpty() && isValidScore) {
            boolean isShow = row.getCell(6).getBooleanCellValue();
            FormType.Category type = getFormType(row.getCell(2).getStringCellValue());

            return new SecondScoreVo(
                    (long) row.getCell(0).getNumericCellValue(),
                    type,
                    isShow ? row.getCell(3).getNumericCellValue() : null,
                    isShow ? row.getCell(4).getNumericCellValue() : null,
                    isShow && type == FormType.Category.MEISTER_TALENT ? row.getCell(5).getNumericCellValue() : null,
                    isShow
            );
        } else {
            return null;
        }
    }

    private List<Cell> validateCellType(Row row) {
        // 수험번호 | 이름 | 전형 구분 | 심층면접 | NCS | 코딩테스트 | 응시 여부
        List<Cell> cellList = new ArrayList<>();

        Cell examinationNumberCell = row.getCell(0);
        Cell nameCell = row.getCell(1);
        Cell typeCell = row.getCell(2);
        Cell depthInterviewScoreCell = row.getCell(3);
        Cell ncsScoreCell = row.getCell(4);
        Cell codingTestScoreCell = row.getCell(5);
        Cell isShowCell = row.getCell(6);
        boolean isShow = false;

        if (examinationNumberCell.getCellType() != CellType.NUMERIC) {
            setErrorCell(examinationNumberCell, "타입 불일치");
            cellList.add(examinationNumberCell);
        }
        if (nameCell.getCellType() != CellType.STRING) {
            setErrorCell(nameCell, "타입 불일치");
            cellList.add(nameCell);
        }
        if (typeCell.getCellType() != CellType.STRING) {
            setErrorCell(typeCell, "타입 불일치");
            cellList.add(typeCell);
        }
        if (isShowCell.getCellType() != CellType.FORMULA) {
            setErrorCell(isShowCell, "타입 불일치");
            cellList.add(isShowCell);
        } else {
            isShow = isShowCell.getBooleanCellValue();
        }

        if (isShow) {
            if (depthInterviewScoreCell != null && depthInterviewScoreCell.getCellType() != CellType.NUMERIC) {
                setErrorCell(depthInterviewScoreCell, "타입 불일치");
                cellList.add(depthInterviewScoreCell);
            }
            if (ncsScoreCell != null && ncsScoreCell.getCellType() != CellType.NUMERIC) {
                setErrorCell(ncsScoreCell, "타입 불일치");
                cellList.add(ncsScoreCell);
            }
            if (
                    ("마이스터인재전형".equals(typeCell.getStringCellValue()) &&
                            (codingTestScoreCell.getCellType() != CellType.NUMERIC)) ||
                    (!"마이스터인재전형".equals(typeCell.getStringCellValue()) &&
                            codingTestScoreCell != null && codingTestScoreCell.getCellType() != CellType.BLANK))
            {
                setErrorCell(codingTestScoreCell, "타입 불일치");
                cellList.add(codingTestScoreCell);
            }
        }

        return cellList;
    }

    private boolean validateScore(Row row, List<Cell> invalidCellTypeList) {
        // 수험번호 | 이름 | 전형 구분 | 심층면접 | NCS | 코딩테스트 | 응시 여부
        boolean isValid = true;

        Cell typeCell = row.getCell(2);
        Cell depthInterviewScoreCell = row.getCell(3);
        Cell ncsScoreCell = row.getCell(4);
        Cell codingTestScoreCell = row.getCell(5);
        boolean isShow = row.getCell(6).getCellType() == CellType.FORMULA && row.getCell(6).getBooleanCellValue();

        if (isShow) {
            String type = typeCell.getStringCellValue();
            double depthInterviewScore = !invalidCellTypeList.contains(depthInterviewScoreCell) ? depthInterviewScoreCell.getNumericCellValue() : 0;
            double ncsScore = !invalidCellTypeList.contains(ncsScoreCell) ? ncsScoreCell.getNumericCellValue() : 0;
            double codingTestScore = codingTestScoreCell != null && !invalidCellTypeList.contains(codingTestScoreCell)
                    ? codingTestScoreCell.getNumericCellValue()
                    : 0;

            switch (type) {
                case "마이스터인재전형" -> {
                    if (!invalidCellTypeList.contains(depthInterviewScoreCell) && !(0 <= depthInterviewScore && depthInterviewScore <= 120)) {
                        setErrorCell(depthInterviewScoreCell, "범위 초과");
                        isValid = false;
                    }
                    if (!invalidCellTypeList.contains(ncsScoreCell) && !(0 <= ncsScore && ncsScore <= 40)) {
                        setErrorCell(ncsScoreCell, "범위 초과");
                        isValid = false;
                    }
                    if (codingTestScoreCell != null && !invalidCellTypeList.contains(codingTestScoreCell)
                            && !(0 <= codingTestScore && codingTestScore <= 80)) {
                        setErrorCell(codingTestScoreCell, "범위 초과");
                        isValid = false;
                    }
                }
                case "사회통합전형" -> {
                    if (!invalidCellTypeList.contains(depthInterviewScoreCell) && !(0 <= depthInterviewScore && depthInterviewScore <= 200)) {
                        setErrorCell(depthInterviewScoreCell, "범위 초과");
                        isValid = false;
                    }
                    if (!invalidCellTypeList.contains(ncsScoreCell) && !(0 <= ncsScore && ncsScore <= 40)) {
                        setErrorCell(ncsScoreCell, "범위 초과");
                        isValid = false;
                    }
                }
                default -> {
                    if (!invalidCellTypeList.contains(depthInterviewScoreCell) && !(0 <= depthInterviewScore && depthInterviewScore <= 120)) {
                        setErrorCell(depthInterviewScoreCell, "범위 초과");
                        isValid = false;
                    }
                    if (!invalidCellTypeList.contains(ncsScoreCell) && !(0 <= ncsScore && ncsScore <= 40)) {
                        setErrorCell(ncsScoreCell, "범위 초과");
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }

    private FormType.Category getFormType(String description) {
        try {
            return FormType.Category.valueOfDescription(description);
        } catch (IllegalArgumentException e) {
            throw new InvalidFileException("지원 전형이 올바르지 않습니다.");
        }
    }

    private static void validateList(List<Form> formList, List<SecondScoreVo> secondScoreVoList) {
        if (formList.size() != secondScoreVoList.size()) {
            throw new InvalidFileException("학생 수가 맞지 않습니다.");
        }
    }

    private void validate(Form form, SecondScoreVo secondScoreVo) {
        if (!form.getExaminationNumber().equals(secondScoreVo.getExaminationNumber())) {
            throw new InvalidFileException("수험번호가 올바르지 않습니다.");
        }
    }

    private void updateFormOrNoShow(Form form, SecondScoreVo secondScoreVo) {
        if (secondScoreVo.isShow()) {
            updateFormSecondRoundScore(form, secondScoreVo);
        } else {
            form.noShow();
        }
    }

    private void updateFormSecondRoundScore(Form form, SecondScoreVo secondScoreVo) {
        if (secondScoreVo.getType().equals(FormType.Category.MEISTER_TALENT)) {
            form.getScore().updateSecondRoundMeisterScore(
                    secondScoreVo.getDepthInterviewScore(),
                    secondScoreVo.getNcsScore(),
                    secondScoreVo.getCodingTestScore()
            );
        } else {
            form.getScore().updateSecondRoundScore(
                    secondScoreVo.getDepthInterviewScore(),
                    secondScoreVo.getNcsScore()
            );
        }
    }

    private void setErrorCell(Cell errorCell, String message) {
        errorCell.setCellStyle(errorCellStyle);
        errorCell.setCellValue(message);
    }
}

@Getter
@AllArgsConstructor
class SecondScoreVo {
    private Long examinationNumber;
    private FormType.Category type;
    private Double depthInterviewScore;
    private Double ncsScore;
    private Double codingTestScore;
    private boolean isShow;
}