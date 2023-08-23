package com.bamdoliro.maru.application.fair;

import com.bamdoliro.maru.domain.fair.domain.Fair;
import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.infrastructure.persistence.fair.FairRepository;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FairFacadeTest {

    @InjectMocks
    private FairFacade fairFacade;

    @Mock
    private FairRepository fairRepository;

    @Test
    void 설명회를_가져온다() {
        // given
        Fair fair = FairFixture.createFair();
        given(fairRepository.findById(fair.getId())).willReturn(Optional.of(fair));

        // when
        Fair foundFair = fairFacade.getFair(fair.getId());

        // then
        assertEquals(fair.getId(), foundFair.getId());
    }

    @Test
    void 존재하지_않는_설명회일_때_에러가_발생한다() {
        // given
        given(fairRepository.findById(anyLong())).willReturn(Optional.empty());

        // when and then
        assertThrows(FairNotFoundException.class, () -> fairFacade.getFair(-1L));
    }
}