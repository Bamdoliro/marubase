package com.bamdoliro.maru.shared.util;

import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveFileUtil {

    public static final String XLSX = "src/test/resources/test.xlsx";
    public static final String PDF = "src/test/resources/test.pdf";

    public static void execute(Resource resource, String filePath) throws IOException {
        try (
                InputStream inputStream = resource.getInputStream();
                OutputStream outputStream = new FileOutputStream(filePath)
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void execute(ByteArrayOutputStream outputStream, String filePath) throws IOException {
        try (
                OutputStream fileOutputStream = new FileOutputStream(filePath)
        ) {
            outputStream.writeTo(fileOutputStream);
            outputStream.close();
        }
    }
}
