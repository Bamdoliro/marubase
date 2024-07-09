package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QueryGenderRatioUseCaseTest {

    @InjectMocks
    private QueryGenderRatioUseCase queryGenderRatioUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 유저가_1차_합격자의_일반_전형_성비를_조회한다() {
        // given
    }
}