package com.bamdoliro.maru.infrastructure.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@RequiredArgsConstructor
@Service
public class GeneratePdfService {

    public void execute(String html) throws IOException, DocumentException {
        String outputFolder = "src/test/resources/test.pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);

        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver()
                .addFont(new ClassPathResource("/static/fonts/SUIT-Bold.ttf")
                                .getURL()
                                .toString(),
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
        renderer.getFontResolver()
                .addFont(new ClassPathResource("/static/fonts/SUIT-SemiBold.ttf")
                                .getURL()
                                .toString(),
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
        renderer.getFontResolver()
                .addFont(new ClassPathResource("/static/fonts/SUIT-Medium.ttf")
                                .getURL()
                                .toString(),
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
        renderer.getFontResolver()
                .addFont(new ClassPathResource("/static/fonts/SUIT-Regular.ttf")
                                .getURL()
                                .toString(),
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
        renderer.getFontResolver()
                .addFont(new ClassPathResource("/static/fonts/SUIT-Light.ttf")
                                .getURL()
                                .toString(),
                        BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);

        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }
}
