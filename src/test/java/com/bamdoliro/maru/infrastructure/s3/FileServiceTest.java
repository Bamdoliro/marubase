package com.bamdoliro.maru.infrastructure.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UrlResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.EmptyFileException;
import com.bamdoliro.maru.infrastructure.s3.exception.FailedToSaveException;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.ImageSizeMismatchException;
import com.bamdoliro.maru.shared.config.properties.S3Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    void 파일을_업로드한다() throws MalformedURLException {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));
        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/random-uuid-image.png";
        given(s3Properties.getBucket()).willReturn("bucket");
        given(amazonS3Client.putObject(any(PutObjectRequest.class))).willReturn(null);
        given(amazonS3Client.getUrl(any(String.class), any(String.class))).willReturn(new URL(url));

        // when
        UrlResponse response = fileService.execute(image, "folder", "form-uuid", file -> {});

        // then
        assertEquals(url, response.getDownloadUrl());
        assertEquals(url, response.getUploadUrl());
        verify(s3Properties, times(3)).getBucket();
        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client, times(2)).getUrl(any(String.class), any(String.class));
    }

    @Test
    void 파일을_업로드할_떄_파일이_비어있으면_에러가_발생한다() {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, "".getBytes(StandardCharsets.UTF_8));

        // when and then
        assertThrows(EmptyFileException.class, () -> fileService.execute(image, "folder", "form-uuid", file -> {
        }));

        verify(s3Properties, never()).getBucket();
        verify(amazonS3Client, never()).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client, never()).getUrl(any(String.class), any(String.class));
    }

    @Test
    void 파일을_업로드할_때_업로드가_실패하면_에러가_발생한다() {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "image.png", MediaType.IMAGE_PNG_VALUE, "<<image>>".getBytes(StandardCharsets.UTF_8));
        given(s3Properties.getBucket()).willReturn("bucket");
        willThrow(AmazonServiceException.class).given(amazonS3Client).putObject(any(PutObjectRequest.class));

        // when and then
        assertThrows(FailedToSaveException.class, () -> fileService.execute(image, "folder", "form-uuid", file -> {
        }));

        verify(s3Properties, times(1)).getBucket();
        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client, never()).getUrl(any(String.class), any(String.class));
    }

    @Test
    void 파일을_업로드할_때_파일_용량이_크면_에러가_발생한다() {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[2 * 1024 * 1024 + 1]
        );

        // when and then
        assertThrows(
                FileSizeLimitExceededException.class,
                () -> fileService.execute(image, "folder", "form-uuid", file -> {
                    if (file.getSize() > 2 * MB) {
                        throw new FileSizeLimitExceededException();
                    }
                })
        );

        // then
        verify(s3Properties, never()).getBucket();
        verify(amazonS3Client, never()).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client, never()).getUrl(any(String.class), any(String.class));
    }

    @Test
    void 파일을_업로드할_때_검증_함수를_통해_이미지_크기를_확인한다() throws IOException {
        // given
        String fileName = "id-picture.png";
        MockMultipartFile image = new MockMultipartFile(
                "image",
                fileName,
                MediaType.IMAGE_PNG_VALUE,
                getFileStreamWith(fileName)
        );

        String url = "https://bucket.s3.ap-northeast-2.amazonaws.com/random-uuid-image.png";
        given(s3Properties.getBucket()).willReturn("bucket");
        given(amazonS3Client.putObject(any(PutObjectRequest.class))).willReturn(null);
        given(amazonS3Client.getUrl(any(String.class), any(String.class))).willReturn(new URL(url));

        // when
        UrlResponse response = fileService.execute(image, "folder", "form-uuid", file -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                if (!(bufferedImage.getWidth() == 117 && bufferedImage.getHeight() == 156)) {
                    throw new ImageSizeMismatchException();
                }
            } catch (IOException e) {
                throw new FailedToSaveException();
            }
        });

        // then
        assertEquals(url, response.getDownloadUrl());
        assertEquals(url, response.getUploadUrl());
        verify(s3Properties, times(3)).getBucket();
        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client, times(2)).getUrl(any(String.class), any(String.class));
    }

    @Test
    void 파일을_업로드할_때_이미지_크기가_크면_에러가_발생한다() throws IOException {
        // given
        String fileName = "id-picture-big.png";
        MockMultipartFile image = new MockMultipartFile(
                "image",
                fileName,
                MediaType.IMAGE_PNG_VALUE,
                getFileStreamWith(fileName)
        );

        // when and then
        assertThrows(
                ImageSizeMismatchException.class,
                () -> fileService.execute(image, "folder", "form-uuid", file -> {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                        if (!(bufferedImage.getWidth() == 117 && bufferedImage.getHeight() == 156)) {
                            throw new ImageSizeMismatchException();
                        }
                    } catch (IOException e) {
                        throw new FailedToSaveException();
                    }
                })
        );

        // then
        verify(s3Properties, never()).getBucket();
        verify(amazonS3Client, never()).putObject(any(PutObjectRequest.class));
        verify(amazonS3Client, never()).getUrl(any(String.class), any(String.class));
    }

    private FileInputStream getFileStreamWith(String fileName) throws FileNotFoundException {
        final String path = String.format("src/test/resources/images/%s", fileName);
        return new FileInputStream(path);
    }
}