package com.bamdoliro.maru.infrastructure.xlsx.exception;

import com.bamdoliro.maru.infrastructure.xlsx.exception.error.XlsxErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class XlsxTemplateLoadFailedException extends MaruException {

    public XlsxTemplateLoadFailedException() {
        super(XlsxErrorProperty.XLSX_TEMPLATE_LOAD_FAILED);
    }
}
