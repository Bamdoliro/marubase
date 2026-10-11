package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.shared.annotation.UseCase;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@UseCase
public class MergeFormPdfUseCase {

    private final FormRepository formRepository;
    private final FileService fileService;
    private final MergePdfService mergePdfService;

    public ByteArrayResource execute(List<Long> formIdList) {
        List<FormUrlVo> formList = formRepository.findFormUrlByFormIdList(formIdList)
                .stream()
                .sorted(Comparator.comparing(
                        FormUrlVo::getExaminationNumber,
                        Comparator.nullsLast(Long::compareTo)
                ))
                .toList();
        validateAllFormsExist(formIdList, formList);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument mergedDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfMerger pdfMerger = new PdfMerger(mergedDocument);

        formList.forEach(form -> mergePdfService.execute(
                pdfMerger,
                fileService.download(FolderConstant.FORM, form.getUuid())
        ));

        mergedDocument.close();
        pdfMerger.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private void validateAllFormsExist(List<Long> formIdList, List<FormUrlVo> formList) {
        long distinctFormCount = formIdList.stream().distinct().count();
        if (formIdList.isEmpty() || formList.size() != distinctFormCount) {
            throw new FormNotFoundException();
        }
    }
}
