package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.FormListRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.config.JasyptConfig;
import com.bamdoliro.maru.shared.config.JpaConfig;
import com.bamdoliro.maru.shared.config.QuerydslConfig;
import com.bamdoliro.maru.shared.config.properties.JasyptProperties;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JasyptConfig.class, JpaConfig.class, QueryAllFormUseCase.class})
@EnableConfigurationProperties(JasyptProperties.class)
class AdminFormDisplayStatusQueryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QueryAllFormUseCase queryAllFormUseCase;

    @Autowired
    private FormRepository formRepository;

    @BeforeEach
    void 모든_상태의_원서를_저장한다() {
        for (FormStatus status : FormStatus.values()) {
            Form form = FormFixture.createForm(FormType.REGULAR);
            ReflectionTestUtils.setField(form, "status", status);
            form.assignExaminationNumber(1001L + status.ordinal());
            entityManager.persist(form.getUser());
            entityManager.persistAndFlush(form);
        }
        entityManager.clear();
    }

    @ParameterizedTest
    @EnumSource(FormStatus.class)
    void 목록_필터는_프론트_표시_기준과_일치한다(FormStatus filter) {
        List<FormSimpleResponse> result = queryAllFormUseCase.execute(
                FormListRequest.builder().status(filter).build());

        List<FormStatus> expected = Arrays.stream(FormStatus.values())
                .filter(status -> matchesFrontendDisplay(filter, status)).toList();
        assertThat(result).extracting(FormSimpleResponse::getStatus)
                .containsExactlyElementsOf(expected);

        // 조회가 저장 상태를 변경하거나 선발/문자용 정확한 상태 조회를 확장하면 안 된다.
        for (FormStatus status : FormStatus.values()) {
            assertThat(formRepository.findByStatus(status)).hasSize(1)
                    .extracting(Form::getStatus).containsExactly(status);
        }
    }

    @Test
    void 상태를_생략하면_모든_원서를_반환한다() {
        assertThat(queryAllFormUseCase.execute(FormListRequest.builder().build()))
                .extracting(FormSimpleResponse::getStatus).containsExactly(FormStatus.values());
    }

    @Test
    void 실제_승인_상태가_없어도_승인으로_표시되는_원서를_조회한다() {
        formRepository.deleteAll(formRepository.findByStatus(FormStatus.APPROVED));
        formRepository.flush();

        assertThat(queryAllFormUseCase.execute(
                FormListRequest.builder().status(FormStatus.APPROVED).build()))
                .extracting(FormSimpleResponse::getStatus)
                .containsExactly(FormStatus.FIRST_PASSED, FormStatus.FIRST_FAILED,
                        FormStatus.PASSED, FormStatus.FAILED, FormStatus.NO_SHOW, FormStatus.ENTERED);
        assertThat(formRepository.findByStatus(FormStatus.APPROVED)).isEmpty();
    }

    private boolean matchesFrontendDisplay(FormStatus filter, FormStatus status) {
        return switch (filter) {
            case FINAL_SUBMITTED -> status != FormStatus.SUBMITTED;
            case APPROVED -> status != FormStatus.SUBMITTED && status != FormStatus.FINAL_SUBMITTED
                    && status != FormStatus.RECEIVED && status != FormStatus.REJECTED;
            case FIRST_PASSED -> status == FormStatus.FIRST_PASSED || status == FormStatus.PASSED
                    || status == FormStatus.FAILED || status == FormStatus.NO_SHOW || status == FormStatus.ENTERED;
            case PASSED -> status == FormStatus.PASSED || status == FormStatus.ENTERED;
            default -> status == filter;
        };
    }
}
