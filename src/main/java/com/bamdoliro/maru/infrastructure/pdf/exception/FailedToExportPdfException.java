package com.bamdoliro.maru.infrastructure.pdf.exception;

import com.bamdoliro.maru.infrastructure.pdf.exception.error.PdfErrorProperty;
import com.bamdoliro.maru.shared.error.MaruException;

public class FailedToExportPdfException extends MaruException {

    public FailedToExportPdfException() {
        super(PdfErrorProperty.FAILED_TO_EXPORT);
    }
}