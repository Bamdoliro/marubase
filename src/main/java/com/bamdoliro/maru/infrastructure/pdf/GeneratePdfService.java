package com.bamdoliro.maru.infrastructure.pdf;

import com.bamdoliro.maru.infrastructure.pdf.exception.FailedToExportPdfException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class GeneratePdfService {

    private final String fontPath = new ClassPathResource("/static/fonts").getPath();
    private final List<String> fonts = List.of(
            "/SUIT-Bold.ttf",
            "/SUIT-SemiBold.ttf",
            "/SUIT-Medium.ttf",
            "/SUIT-Regular.ttf",
            "/SUIT-Light.ttf"
    );
    private final ConverterProperties converterProperties = createConverterProperties();

    public ByteArrayOutputStream execute(String html) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, outputStream, converterProperties);
        return outputStream;
    }

    private ConverterProperties createConverterProperties() {
        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);

        fonts.forEach(font -> {
            try {
                FontProgram fontProgram = FontProgramFactory.createFont(fontPath + font);
                fontProvider.addFont(fontProgram);
            } catch (IOException e) {
                throw new FailedToExportPdfException();
            }
        });

        properties.setFontProvider(fontProvider);
        return properties;
    }
}
