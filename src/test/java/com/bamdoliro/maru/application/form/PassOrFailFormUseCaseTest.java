package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.service.CalculateFormScoreService;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.user.UserRepository;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormListRequest;
import com.bamdoliro.maru.presentation.form.dto.request.PassOrFailFormRequest;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
class PassOrFailFormUseCaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalculateFormScoreService calculateFormScoreService;

    @Autowired
    private PassOrFailFormUseCase passOrFailFormUseCase;

    @Autowired
    private FormRepository formRepository;

    @Test
    void 정상적으로_합격자를_처리한다() {
        // given
        for (int i = 0; i < 3; i++) {
            User user = userRepository.save(UserFixture.createUser());
            Form form = FormFixture.createRandomForm(user);
            calculateFormScoreService.execute(form);
            formRepository.save(form);
        }

        PassOrFailFormListRequest request = new PassOrFailFormListRequest(
                List.of(
                        new PassOrFailFormRequest(3L, true),
                        new PassOrFailFormRequest(1L, false),
                        new PassOrFailFormRequest(2L, true)
                )
        );

        // when
        passOrFailFormUseCase.execute(request);

        // then
        assertAll(
                () -> assertEquals(FormStatus.FAILED, formRepository.findById(1L).get().getStatus()),
                () -> assertEquals(FormStatus.PASSED, formRepository.findById(2L).get().getStatus()),
                () -> assertEquals(FormStatus.PASSED, formRepository.findById(3L).get().getStatus())
        );
    }

    @Test
    void 존재하지_않는_원서를_처리하려고_하면_에러가_발생한다() {
        // given
        for (int i = 0; i < 3; i++) {
            User user = userRepository.save(UserFixture.createUser());
            Form form = FormFixture.createRandomForm(user);
            calculateFormScoreService.execute(form);
            formRepository.save(form);
        }

        PassOrFailFormListRequest request = new PassOrFailFormListRequest(
                List.of(
                        new PassOrFailFormRequest(3L, true),
                        new PassOrFailFormRequest(1L, false),
                        new PassOrFailFormRequest(2L, true),
                        new PassOrFailFormRequest(4L, false)
                )
        );

        // when and then
        assertThrows(IllegalArgumentException.class,
                () -> passOrFailFormUseCase.execute(request));
    }
}