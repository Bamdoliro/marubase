package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.shared.config.JasyptConfig;
import com.bamdoliro.maru.shared.config.JpaConfig;
import com.bamdoliro.maru.shared.config.QuerydslConfig;
import com.bamdoliro.maru.shared.config.properties.JasyptProperties;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JasyptConfig.class, JpaConfig.class})
@EnableConfigurationProperties(JasyptProperties.class)
class FormStatusInQueryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FormRepository formRepository;

    @Test
    void 주어진_상태들에_해당하는_원서만_조회한다() {
        Form approved = saveForm(Form::approve);
        Form firstPassed = saveForm(Form::firstPass);
        Form firstFailed = saveForm(Form::firstFail);
        saveForm(form -> {});
        saveForm(Form::submit);
        saveForm(Form::reject);
        saveForm(Form::receive);
        saveForm(Form::pass);
        saveForm(Form::fail);
        saveForm(Form::noShow);
        saveForm(Form::enter);

        assertThat(formRepository.findByStatusIn(Set.of(
                FormStatus.APPROVED,
                FormStatus.FIRST_PASSED,
                FormStatus.FIRST_FAILED
        ))).containsExactlyInAnyOrder(approved, firstPassed, firstFailed);
    }

    @Test
    void 해당하는_원서가_없으면_빈_목록을_반환한다() {
        saveForm(Form::submit);

        assertThat(formRepository.findByStatusIn(Set.of(FormStatus.APPROVED))).isEmpty();
    }

    private Form saveForm(Consumer<Form> changeStatus) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        changeStatus.accept(form);
        entityManager.persist(form.getUser());
        entityManager.persistAndFlush(form);
        return form;
    }
}
