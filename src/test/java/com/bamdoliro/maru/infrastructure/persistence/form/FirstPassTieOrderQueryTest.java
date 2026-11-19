package com.bamdoliro.maru.infrastructure.persistence.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Score;
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

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QuerydslConfig.class, JasyptConfig.class, JpaConfig.class})
@EnableConfigurationProperties(JasyptProperties.class)
class FirstPassTieOrderQueryTest {

    private static final int REPEAT_COUNT = 5;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FormRepository formRepository;

    @Test
    void 특별전형_승인_원서는_점수_내림차순이고_동점이면_id_오름차순이다() {
        Form firstTie = saveApprovedForm(FormType.MEISTER_TALENT, 90.0);
        Form highest = saveApprovedForm(FormType.ONE_PARENT, 95.0);
        Form secondTie = saveApprovedForm(FormType.MULTI_CHILDREN, 90.0);
        Form lowest = saveApprovedForm(FormType.MEISTER_TALENT, 80.0);
        Form thirdTie = saveApprovedForm(FormType.ONE_PARENT, 90.0);

        assertOrder(
                formRepository::findApprovedSpecialForm,
                List.of(highest, firstTie, secondTie, thirdTie, lowest)
        );
    }

    @Test
    void 일반전형_승인_원서는_점수_내림차순이고_동점이면_id_오름차순이다() {
        Form firstTie = saveApprovedForm(FormType.REGULAR, 90.0);
        Form highest = saveApprovedForm(FormType.REGULAR, 95.0);
        Form secondTie = saveApprovedForm(FormType.REGULAR, 90.0);
        Form lowest = saveApprovedForm(FormType.REGULAR, 80.0);
        Form thirdTie = saveApprovedForm(FormType.REGULAR, 90.0);

        assertOrder(
                formRepository::findApprovedRegularForm,
                List.of(highest, firstTie, secondTie, thirdTie, lowest)
        );
    }

    @Test
    void 정원_외_전형_승인_원서는_점수_내림차순이고_동점이면_id_오름차순이다() {
        Form firstTie = saveApprovedForm(FormType.SPECIAL_ADMISSION, 90.0);
        Form highest = saveApprovedForm(FormType.NATIONAL_VETERANS_EDUCATION, 95.0);
        Form secondTie = saveApprovedForm(FormType.NATIONAL_VETERANS_EDUCATION, 90.0);
        Form lowest = saveApprovedForm(FormType.SPECIAL_ADMISSION, 80.0);
        Form thirdTie = saveApprovedForm(FormType.SPECIAL_ADMISSION, 90.0);

        assertOrder(
                formRepository::findApprovedSupernumeraryForm,
                List.of(highest, firstTie, secondTie, thirdTie, lowest)
        );
    }

    private void assertOrder(Supplier<List<Form>> query, List<Form> expected) {
        List<Long> expectedIds = expected.stream().map(Form::getId).toList();

        for (int i = 0; i < REPEAT_COUNT; i++) {
            entityManager.clear();
            List<Long> actualIds = query.get().stream().map(Form::getId).toList();
            assertThat(actualIds).containsExactlyElementsOf(expectedIds);
        }
    }

    private Form saveApprovedForm(FormType type, Double firstRoundScore) {
        Form form = FormFixture.createForm(type);
        form.updateScore(new Score(firstRoundScore, 0, 0, 0));
        form.approve();
        entityManager.persist(form.getUser());
        entityManager.persistAndFlush(form);
        return form;
    }
}
