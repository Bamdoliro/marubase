package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.GradeVo;
import com.bamdoliro.maru.presentation.analysis.dto.request.GradeDistributionRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.GradeDistributionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryGradeDistributionUseCaseTest {

    @InjectMocks
    private QueryGradeDistributionUseCase queryGradeDistributionUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 어드민이_1차_합격자들의_성적_분포를_조회한다() {
        // given
        List<GradeVo> voList = List.of(
                new GradeVo(FormType.REGULAR, 238.241, 160.278, 208.963, null, null, null),
                new GradeVo(FormType.MEISTER_TALENT, 158.758, 103.004, 147.213, null, null, null),
                new GradeVo(FormType.NATIONAL_BASIC_LIVING, 158.758, 103.004, 147.213, null, null, null),
                new GradeVo(FormType.FROM_NORTH_KOREA, 158.758, 103.004, 147.213, null, null, null),
                new GradeVo(FormType.SPECIAL_ADMISSION, 158.758, 103.004, 147.213, null, null, null)
        );
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findGradeGroupByTypeAndStatus(round)).willReturn(voList);
        GradeDistributionRequest request = new GradeDistributionRequest(round);
        
        // when
        List<GradeDistributionResponse> responseList = queryGradeDistributionUseCase.execute(request);

        // then
        assertEquals(responseList.size(), FormType.values().length);
        assertEquals(responseList.stream()
                .filter(response -> response.getFirstRoundAvg() != 0)
                .toList()
                .size(), voList.size());
        verify(formRepository, times(1)).findGradeGroupByTypeAndStatus(round);
    }

    @Test
    void 어드민이_2차_전형자들의_성적_분포를_조회한다() {
        // given
        List<GradeVo> voList = List.of(
                new GradeVo(FormType.REGULAR, 238.241, 160.278, 208.963, 208.758, 153.004, 197.213),
                new GradeVo(FormType.MEISTER_TALENT, 158.758, 103.004, 147.213, 208.758, 153.004, 197.213),
                new GradeVo(FormType.NATIONAL_BASIC_LIVING, 158.758, 103.004, 147.213, 208.758, 153.004, 197.213),
                new GradeVo(FormType.FROM_NORTH_KOREA, 158.758, 103.004, 147.213, 208.758, 153.004, 197.213),
                new GradeVo(FormType.SPECIAL_ADMISSION, 149.559, 149.559, 149.559, 199.559, 199.559, 199.559)
        );
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findGradeGroupByTypeAndStatus(round)).willReturn(voList);
        GradeDistributionRequest request = new GradeDistributionRequest(round);

        // when
        List<GradeDistributionResponse> responseList = queryGradeDistributionUseCase.execute(request);

        // then
        assertEquals(responseList.size(), FormType.values().length);
        assertEquals(responseList.stream()
                .filter(response -> response.getFirstRoundAvg() != 0)
                .toList()
                .size(), voList.size());
        verify(formRepository, times(1)).findGradeGroupByTypeAndStatus(round);
    }

    @Test
    void 어드민이_최종_합격자들의_성적_분포를_조회한다() {
        // given
        List<GradeVo> voList = List.of(
                new GradeVo(FormType.REGULAR, 238.241, 160.278, 208.963, 208.758, 153.004, 197.213),
                new GradeVo(FormType.MEISTER_TALENT, 158.758, 103.004, 147.213, 208.758, 153.004, 197.213),
                new GradeVo(FormType.NATIONAL_BASIC_LIVING, 158.758, 103.004, 147.213, 208.758, 153.004, 197.213),
                new GradeVo(FormType.FROM_NORTH_KOREA, 158.758, 103.004, 147.213, 208.758, 153.004, 197.213),
                new GradeVo(FormType.SPECIAL_ADMISSION, 149.559, 149.559, 149.559, 199.559, 199.559, 199.559)
        );
        List<FormStatus> round = List.of(FormStatus.PASSED);
        given(formRepository.findGradeGroupByTypeAndStatus(round)).willReturn(voList);
        GradeDistributionRequest request = new GradeDistributionRequest(round);

        // when
        List<GradeDistributionResponse> responseList = queryGradeDistributionUseCase.execute(request);

        // then
        assertEquals(responseList.size(), FormType.values().length);
        assertEquals(responseList.stream()
                .filter(response -> response.getFirstRoundAvg() != 0)
                .toList()
                .size(), voList.size());
        verify(formRepository, times(1)).findGradeGroupByTypeAndStatus(round);
    }
}