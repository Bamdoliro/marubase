package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.infrastructure.pdf.MergePdfService;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.FormUrlVo;
import com.bamdoliro.maru.infrastructure.s3.FileService;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.itextpdf.kernel.utils.PdfMerger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MergeFormPdfUseCaseTest {

    @InjectMocks
    private MergeFormPdfUseCase mergeFormPdfUseCase;

    @Mock
    private FormRepository formRepository;

    @Mock
    private FileService fileService;

    @Mock
    private MergePdfService mergePdfService;

    @Test
    void 선택한_원서의_pdf를_병합한다() {
        // given
        List<Long> formIdList = List.of(1L, 2L, 3L);
        List<FormUrlVo> formList = List.of(
                FormFixture.createFormUrlVo(),
                FormFixture.createFormUrlVo(),
                FormFixture.createFormUrlVo()
        );
        given(formRepository.findFormUrlByFormIdList(formIdList)).willReturn(formList);
        given(fileService.download(anyString(), anyString())).willReturn(new byte[0]);

        // when
        mergeFormPdfUseCase.execute(formIdList);

        // then
        verify(formRepository, times(1)).findFormUrlByFormIdList(formIdList);
        verify(fileService, times(3)).download(anyString(), anyString());
        verify(mergePdfService, times(3)).execute(any(PdfMerger.class), any(byte[].class));
    }

    @Test
    void 선택한_원서가_존재하지_않으면_에러가_발생한다() {
        // given
        List<Long> formIdList = List.of(1L, 2L);
        given(formRepository.findFormUrlByFormIdList(formIdList)).willReturn(List.of(
                FormFixture.createFormUrlVo()
        ));

        // when and then
        assertThrows(FormNotFoundException.class, () -> mergeFormPdfUseCase.execute(formIdList));
    }

    @Test
    void 선택한_원서가_없으면_에러가_발생한다() {
        // given
        List<Long> formIdList = List.of();
        given(formRepository.findFormUrlByFormIdList(formIdList)).willReturn(List.of());

        // when and then
        assertThrows(FormNotFoundException.class, () -> mergeFormPdfUseCase.execute(formIdList));
    }
}
