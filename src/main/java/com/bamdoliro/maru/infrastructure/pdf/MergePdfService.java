package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.infrastructure.pdf.exception.FailedToExportPdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.PdfMerger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class MergePdfService {

    public void execute(PdfMerger merger, ByteArrayOutputStream outputStream) {
        PdfDocument document = createPdfDocumentFrom(outputStream);

        merger.merge(document, 1, document.getNumberOfPages());
        document.close();

        try {
            outputStream.close();
        } catch (IOException e) {
            throw new FailedToExportPdfException();
        }
    }

    private PdfDocument createPdfDocumentFrom(ByteArrayOutputStream outputStream) {
        try {
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return new PdfDocument(new PdfReader(inputStream));
        } catch (IOException e) {
            throw new FailedToExportPdfException();
        }
    }
}
