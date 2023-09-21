package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormUrlResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryFormUrlUseCaseTest {

    @InjectMocks
    private QueryFormUrlUseCase queryFormUrlUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 원서_url을_조회한다() {
        // given
        List<Long> idList = List.of(1L, 2L, 3L);
        given(formRepository.findFormUrlByFormIdList(idList)).willReturn(List.of(
                FormFixture.createFormUrlVo(),
                FormFixture.createFormUrlVo(),
                FormFixture.createFormUrlVo()
        ));

        // when
        List<FormUrlResponse> responseList = queryFormUrlUseCase.execute(idList);

        // then
        assertEquals(idList.size(), responseList.size());

        verify(formRepository, times(1)).findFormUrlByFormIdList(idList);
    }
}