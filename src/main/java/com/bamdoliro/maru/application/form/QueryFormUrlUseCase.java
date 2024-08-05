package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.presentation.form.dto.response.FormUrlResponse;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@UseCase
public class QueryFormUrlUseCase {

    private final FormRepository formRepository;
    private final FileService fileService;

    public List<FormUrlResponse> execute(List<Long> formIdList) {
        return formRepository.findFormUrlByFormIdList(formIdList).stream()
                .map(vo -> new FormUrlResponse(
                        vo.getExaminationNumber(),
                        vo.getName(),
                        fileService.getPresignedUrl(FolderConstant.FORM, vo.getUuid()).getDownloadUrl()
                )).toList();
    }
}
