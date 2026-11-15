package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.exception.CannotGenerateUniqueTestDataException;
import com.bamdoliro.maru.domain.form.service.AssignExaminationNumberService;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.form.service.RandomFormGenerator;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.GenerateTestFormRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenerateTestFormUseCaseRetryTest {

    @InjectMocks
    private GenerateTestFormUseCase generateTestFormUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FormRepository formRepository;

    @Mock
    private RandomFormGenerator randomFormGenerator;

    @Mock
    private CalculateFormScoreService calculateFormScoreService;

    @Mock
    private AssignExaminationNumberService assignExaminationNumberService;

    @Test
    void 전화번호가_계속_중복되면_재시도_횟수를_넘긴_뒤_에러가_발생한다() {
        // given
        GenerateTestFormListRequest request = new GenerateTestFormListRequest(List.of(
                new GenerateTestFormRequest(FormType.REGULAR, 1)
        ));
        given(randomFormGenerator.randomPhoneNumber()).willReturn("01012345678");
        given(userRepository.existsByPhoneNumber("01012345678")).willReturn(true);

        // when and then
        assertThrows(CannotGenerateUniqueTestDataException.class, () -> generateTestFormUseCase.execute(request));
        verify(userRepository, times(GenerateTestFormUseCase.MAX_PHONE_NUMBER_RETRY)).existsByPhoneNumber("01012345678");
        verify(userRepository, never()).saveAll(anyList());
        verify(formRepository, never()).saveAll(anyList());
        verify(assignExaminationNumberService, never()).executeAll(any());
    }
}
