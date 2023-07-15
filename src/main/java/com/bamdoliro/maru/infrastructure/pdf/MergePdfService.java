package com.bamdoliro.maru.infrastructure.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.PdfMerger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class MergePdfService {

    public void execute(PdfMerger merger, ByteArrayOutputStream outputStream) {
        PdfDocument document = createPdfDocumentFrom(outputStream);

        if (Objects.nonNull(document)) {
            merger.merge(document, 1, document.getNumberOfPages());
            document.close();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PdfDocument createPdfDocumentFrom(ByteArrayOutputStream outputStream) {
        try {
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return new PdfDocument(new PdfReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
