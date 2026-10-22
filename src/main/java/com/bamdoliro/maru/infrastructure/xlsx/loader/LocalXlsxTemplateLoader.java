package com.bamdoliro.maru.infrastructure.xlsx.loader;

import com.bamdoliro.maru.infrastructure.xlsx.exception.XlsxTemplateLoadFailedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Profile("!prod & !dev")
@Component
public class LocalXlsxTemplateLoader implements XlsxTemplateLoader {

    private final Path localDir;

    public LocalXlsxTemplateLoader(@Value("${maru.xlsx.local-dir:./local-templates}") String localDir) {
        this.localDir = Path.of(localDir);
    }

    @Override
    public Workbook open(String fileName) {
        Path path = localDir.resolve(fileName);
        try (InputStream inputStream = Files.newInputStream(path)) {
            return new XSSFWorkbook(inputStream);
        } catch (IOException | RuntimeException e) {
            log.error("엑셀 양식을 불러오지 못했습니다. path={}, error={}", path, e.toString());
            throw new XlsxTemplateLoadFailedException();
        }
    }
}
