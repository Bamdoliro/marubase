package com.bamdoliro.maru.infrastructure.xlsx.loader;

import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.xlsx.exception.XlsxTemplateLoadFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Profile({"prod", "dev"})
@Component
public class S3XlsxTemplateLoader implements XlsxTemplateLoader {

    private final FileService fileService;

    @Override
    public Workbook open(String fileName) {
        try {
            byte[] template = fileService.download(FolderConstant.XLSX_TEMPLATES, fileName);
            return new XSSFWorkbook(new ByteArrayInputStream(template));
        } catch (IOException | RuntimeException e) {
            log.error("엑셀 양식을 불러오지 못했습니다. key={}/{}, error={}", FolderConstant.XLSX_TEMPLATES, fileName, e.toString());
            throw new XlsxTemplateLoadFailedException();
        }
    }
}
