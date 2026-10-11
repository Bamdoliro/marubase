package com.bamdoliro.maru.infrastructure.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MergePdfServiceTest {

    private final MergePdfService mergePdfService = new MergePdfService();

    @Test
    void 여러_pdf를_하나로_병합한다() throws Exception {
        // given
        byte[] firstPdf = createPdf();
        byte[] secondPdf = createPdf();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        // when
        mergePdfService.execute(pdfMerger, firstPdf);
        mergePdfService.execute(pdfMerger, secondPdf);
        mergedDocument.close();
        pdfMerger.close();

        // then
        PdfDocument result = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())));
        assertEquals(2, result.getNumberOfPages());
        result.close();
    }

    private byte[] createPdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream));
        document.addNewPage();
        document.close();

        return outputStream.toByteArray();
    }
}
