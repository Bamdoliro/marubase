package com.bamdoliro.maru.infrastructure.xlsx.loader;

import org.apache.poi.ss.usermodel.Workbook;

public interface XlsxTemplateLoader {

    Workbook open(String fileName);
}
