package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryAllFormUseCaseTest {

    @InjectMocks
    private QueryAllFormUseCase queryAllFormUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 모든_원서를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        formList.forEach(form -> form.assignExaminationNumber(1001L));

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, null, null);

        // then
        assertEquals(formList.size(), returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 특별전형_원서만_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        formList.forEach(form -> form.assignExaminationNumber(1001L));

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, FormType.MEISTER_TALENT, null);

        // then
        assertEquals(1, returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 사회통합전형_원서만_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        formList.forEach(form -> form.assignExaminationNumber(1001L));

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, FormType.MULTI_CHILDREN, null);

        // then
        assertEquals(1, returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 최종_점수가_높은_순으로_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        formList.stream()
                .filter(form -> form.getType() == FormType.MEISTER_TALENT)
                .forEach(form -> form.getScore().updateSecondRoundMeisterScore(
                        RandomUtil.randomDouble(10, 50),
                        RandomUtil.randomDouble(10, 50),
                        RandomUtil.randomDouble(10, 50)
                ));
        formList.stream()
                .filter(form -> form.getType() != FormType.MEISTER_TALENT)
                .forEach(form -> form.getScore().updateSecondRoundScore(
                        RandomUtil.randomDouble(10, 50),
                        RandomUtil.randomDouble(10, 50)
                ));
        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, null, "total-score-desc");

        // then
        assertEquals(
                Collections.max(formList, Comparator.comparingDouble(form -> form.getScore().getTotalScore()))
                        .getScore()
                        .getTotalScore(),
                returnedFormList.get(0).getTotalScore()
        );

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 최종_점수가_낮은_순으로_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        formList.stream()
                .filter(form -> form.getType() == FormType.MEISTER_TALENT)
                .forEach(form -> form.getScore().updateSecondRoundMeisterScore(
                        RandomUtil.randomDouble(10, 50),
                        RandomUtil.randomDouble(10, 50),
                        RandomUtil.randomDouble(10, 50)
                ));
        formList.stream()
                .filter(form -> form.getType() != FormType.MEISTER_TALENT)
                .forEach(form -> form.getScore().updateSecondRoundScore(
                        RandomUtil.randomDouble(10, 50),
                        RandomUtil.randomDouble(10, 50)
                ));
        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, null, "total-score-asc");

        // then
        assertEquals(
                Collections.min(formList, Comparator.comparingDouble(form -> form.getScore().getTotalScore()))
                        .getScore()
                        .getTotalScore(),
                returnedFormList.get(0).getTotalScore()
        );

        verify(formRepository, times(1)).findByStatus(null);
    }

    @Test
    void 접수번호가_높은_순으로_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.MULTI_CHILDREN)
        );

        given(formRepository.findByStatus(null)).willReturn(formList);

        // when
        List<FormSimpleResponse> returnedFormList = queryAllFormUseCase.execute(null, FormType.REGULAR, "form-id");

        // then
        assertEquals(1, returnedFormList.size());

        verify(formRepository, times(1)).findByStatus(null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round-score-asc", "first-round-score-desc"})
    void 최종_점수가_없어도_일차_점수로_정렬한다(String sort) {
        Form high = createScoredForm(187.0, null, FormType.REGULAR);
        Form low = createScoredForm(0.0, null, FormType.REGULAR);
        Form middle = createScoredForm(186.033, null, FormType.REGULAR);
        given(formRepository.findByStatus(null)).willReturn(List.of(middle, high, low));

        List<FormSimpleResponse> result = queryAllFormUseCase.execute(null, null, sort);

        assertThat(result).extracting(FormSimpleResponse::getFirstRoundScore)
                .containsExactlyElementsOf(sort.endsWith("asc")
                        ? List.of(0.0, 186.033, 187.0) : List.of(187.0, 186.033, 0.0));
        assertThat(result).extracting(FormSimpleResponse::getTotalScore).containsOnlyNulls();
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round-score-asc", "first-round-score-desc"})
    void 일차_점수_정렬은_최종_점수에_영향받지_않는다(String sort) {
        Form high = createScoredForm(187.0, 200.0, FormType.REGULAR);
        Form low = createScoredForm(186.033, 250.0, FormType.REGULAR);
        given(formRepository.findByStatus(null)).willReturn(List.of(low, high));

        List<FormSimpleResponse> result = queryAllFormUseCase.execute(null, null, sort);

        assertThat(result).extracting(FormSimpleResponse::getFirstRoundScore)
                .containsExactlyElementsOf(sort.endsWith("asc")
                        ? List.of(186.033, 187.0) : List.of(187.0, 186.033));
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round-score-asc", "first-round-score-desc"})
    void 일차_점수가_없으면_양방향_정렬에서_마지막에_배치한다(String sort) {
        Form missing = createScoredForm(null, null, FormType.REGULAR);
        Form scored = createScoredForm(0.0, null, FormType.REGULAR);
        given(formRepository.findByStatus(null)).willReturn(List.of(missing, scored));

        List<FormSimpleResponse> result = queryAllFormUseCase.execute(null, null, sort);

        assertThat(result).extracting(FormSimpleResponse::getFirstRoundScore)
                .containsExactly(0.0, null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round-score-asc", "first-round-score-desc"})
    void 일차_점수_정렬에도_상태와_전형_필터를_적용한다(String sort) {
        Form high = createScoredForm(187.0, null, FormType.REGULAR);
        Form low = createScoredForm(186.033, null, FormType.REGULAR);
        Form meister = createScoredForm(200.0, null, FormType.MEISTER_TALENT);
        given(formRepository.findByStatus(FormStatus.SUBMITTED)).willReturn(List.of(high, meister, low));

        List<FormSimpleResponse> result = queryAllFormUseCase.execute(FormStatus.SUBMITTED, FormType.REGULAR, sort);

        assertThat(result).extracting(FormSimpleResponse::getType).containsOnly(FormType.REGULAR);
        assertThat(result).extracting(FormSimpleResponse::getFirstRoundScore)
                .containsExactlyElementsOf(sort.endsWith("asc")
                        ? List.of(186.033, 187.0) : List.of(187.0, 186.033));
        verify(formRepository).findByStatus(FormStatus.SUBMITTED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round-score-asc", "first-round-score-desc"})
    void 일차_점수_정렬_대상이_없으면_빈_목록을_반환한다(String sort) {
        given(formRepository.findByStatus(null)).willReturn(List.of());

        assertThat(queryAllFormUseCase.execute(null, null, sort)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round-score-asc", "first-round-score-desc"})
    void 일차_점수_동점자는_기존_조회_순서를_유지한다(String sort) {
        Form first = createScoredForm(187.0, 200.0, FormType.REGULAR);
        Form second = createScoredForm(187.0, 250.0, FormType.REGULAR);
        first.assignExaminationNumber(1001L);
        second.assignExaminationNumber(1002L);
        given(formRepository.findByStatus(null)).willReturn(List.of(first, second));

        assertThat(queryAllFormUseCase.execute(null, null, sort))
                .extracting(FormSimpleResponse::getExaminationNumber).containsExactly(1001L, 1002L);
    }

    private Form createScoredForm(Double firstRoundScore, Double totalScore, FormType type) {
        Form form = FormFixture.createForm(type);
        form.updateScore(new Score(0.0, null, 0, 0, 0,
                null, null, null, firstRoundScore, totalScore));
        return form;
    }
}
