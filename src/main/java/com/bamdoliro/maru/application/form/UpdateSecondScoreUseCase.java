package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.InvalidFileException;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@UseCase
public class UpdateSecondScoreUseCase {

    private final FormRepository formRepository;

    @Transactional
    public void execute(MultipartFile xlsx) throws IOException {
        Workbook workbook = new XSSFWorkbook(xlsx.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        List<Form> formList = formRepository.findByStatus(FormStatus.FIRST_PASSED);

        List<SecondScoreVo> secondScoreVoList = IntStream.range(1, sheet.getPhysicalNumberOfRows())
                .mapToObj(sheet::getRow)
                .map(this::getSecondScoreFrom)
                .sorted(Comparator.comparingLong(SecondScoreVo::getExaminationNumber))
                .toList();

        int formIndex = 0;
        for (SecondScoreVo secondScoreVo : secondScoreVoList) {
            Form form = formList.get(formIndex);

            while (form.getExaminationNumber() <= secondScoreVo.getExaminationNumber()) {
                if (form.getExaminationNumber().equals(secondScoreVo.getExaminationNumber())) {
                    updateFormSecondRoundScore(form, secondScoreVo);
                    formIndex++;
                } else {
                    form.noShow();
                }
            }
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

    private SecondScoreVo getSecondScoreFrom(Row row) {
        validateRow(row);

        FormType.Category type = getFormType(row.getCell(2).getStringCellValue());
        return new SecondScoreVo(
                (long) row.getCell(0).getNumericCellValue(),
                type,
                row.getCell(3).getNumericCellValue(),
                row.getCell(4).getNumericCellValue(),
                type == FormType.Category.MEISTER_TALENT ? row.getCell(5).getNumericCellValue() : null
        );
    }

    private void validateRow(Row row) {
        // 수험번호 | 이름 | 전형 구분 | 심층면접 | NCS | 코딩테스트
        if (
                row.getCell(0).getCellType() == CellType.NUMERIC &&
                        row.getCell(1).getCellType() == CellType.STRING &&
                        row.getCell(2).getCellType() == CellType.STRING &&
                        row.getCell(3).getCellType() == CellType.NUMERIC &&
                        row.getCell(4).getCellType() == CellType.NUMERIC &&
                        (row.getCell(5).getCellType() == CellType.NUMERIC ||
                                row.getCell(5).getCellType() == CellType.BLANK)
        ) {
            throw new InvalidFileException();
        }
    }

    private FormType.Category getFormType(String description) {
        try {
            return FormType.Category.valueOfDescription(description);
        } catch (IllegalArgumentException e) {
            throw new InvalidFileException();
        }
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
}