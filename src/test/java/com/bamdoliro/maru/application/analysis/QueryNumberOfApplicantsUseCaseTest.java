package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.NumberOfApplicantsVo;
import com.bamdoliro.maru.presentation.analysis.dto.response.NumberOfApplicantsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryNumberOfApplicantsUseCaseTest {

    @InjectMocks
    private QueryNumberOfApplicantsUseCase queryNumberOfApplicantsUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 전형별_지원자_수를_조회한다() {
        // given
        List<NumberOfApplicantsVo> voList = List.of(
                new NumberOfApplicantsVo(FormType.REGULAR, 1L),
                new NumberOfApplicantsVo(FormType.MEISTER_TALENT, 1L),
                new NumberOfApplicantsVo(FormType.NATIONAL_BASIC_LIVING, 1L),
                new NumberOfApplicantsVo(FormType.FROM_NORTH_KOREA, 1L),
                new NumberOfApplicantsVo(FormType.SPECIAL_ADMISSION, 1L)
                );
        given(formRepository.findTypeAndCountGroupByType()).willReturn(voList);

        // when
        List<NumberOfApplicantsResponse> responseList = queryNumberOfApplicantsUseCase.execute("CURRENT");

        // then
        assertEquals(responseList.size(), FormType.values().length);
        assertEquals(responseList
                .stream()
                .mapToLong(NumberOfApplicantsResponse::getCount)
                .sum(), 5L);

        verify(formRepository, times(1)).findTypeAndCountGroupByType();
    }
}