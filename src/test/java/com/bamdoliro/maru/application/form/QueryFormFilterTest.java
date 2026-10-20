package com.bamdoliro.maru.application.form;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.value.Score;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.form.dto.request.FormListRequest;
import com.bamdoliro.maru.presentation.form.dto.response.FormSimpleResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryFormFilterTest {

    @Mock
    private FormRepository formRepository;

    @InjectMocks
    private QueryAllFormUseCase useCase;

    @Test
    void 멘토멘티_체크_미체크_전체를_구분한다() {
        given(formRepository.findByStatus(null)).willReturn(List.of(
                form(1L, true, "부산중", 180.0, null),
                form(2L, false, "서울중", 185.0, null)));

        assertIds(FormListRequest.builder().mentoringProgram(true).build(), 1L);
        assertIds(FormListRequest.builder().mentoringProgram(false).build(), 2L);
        assertIds(FormListRequest.builder().build(), 1L, 2L);
    }

    @Test
    void 학교명_부분일치_검색에서_공백과_학교명_누락을_처리한다() {
        given(formRepository.findByStatus(null)).willReturn(List.of(
                form(1L, false, "부산중학교", 180.0, null),
                form(2L, false, "해강중학교", 185.0, null),
                form(3L, false, null, 190.0, null)));

        assertIds(FormListRequest.builder().schoolName(" 부산 ").build(), 1L);
        assertIds(FormListRequest.builder().schoolName("중학교").build(), 1L, 2L);
        assertIds(FormListRequest.builder().schoolName("   ").build(), 1L, 2L, 3L);
        assertIds(FormListRequest.builder().schoolName("없는학교").build());
    }

    @Test
    void 학교_정보_전체가_없으면_학교명_검색에서_제외한다() {
        Form missingSchool = form(1L, false, null, 180.0, null);
        ReflectionTestUtils.setField(missingSchool.getEducation(), "school", null);
        given(formRepository.findByStatus(null)).willReturn(List.of(missingSchool));

        assertIds(FormListRequest.builder().schoolName("부산").build());
    }

    @Test
    void 일차_점수_범위는_양끝을_포함하고_미정은_제외한다() {
        given(formRepository.findByStatus(null)).willReturn(List.of(
                form(1L, false, "부산중", 0.0, null),
                form(2L, false, "부산중", 180.0, null),
                form(3L, false, "부산중", 186.033, null),
                form(4L, false, "부산중", null, null)));

        assertIds(FormListRequest.builder().firstRoundScoreMin(180.0).firstRoundScoreMax(186.033).build(), 2L, 3L);
        assertIds(FormListRequest.builder().firstRoundScoreMax(0.0).build(), 1L);
        assertIds(FormListRequest.builder().firstRoundScoreMin(186.033).build(), 3L);
    }

    @Test
    void 최종_점수_범위는_일차_점수로_대체하지_않는다() {
        given(formRepository.findByStatus(null)).willReturn(List.of(
                form(1L, false, "부산중", 190.0, null),
                form(2L, false, "부산중", 180.0, 200.0),
                form(3L, false, "부산중", 180.0, 250.0),
                form(4L, false, "부산중", 0.0, 0.0)));

        assertIds(FormListRequest.builder().totalScoreMin(200.0).totalScoreMax(250.0).build(), 2L, 3L);
        assertIds(FormListRequest.builder().totalScoreMax(0.0).build(), 4L);
        assertIds(FormListRequest.builder().totalScoreMin(250.0).build(), 3L);
    }

    @Test
    void 모든_검색조건을_AND로_결합한_후_정렬한다() {
        Form first = form(1L, true, "부산중", 180.0, 220.0);
        Form second = form(2L, true, "부산중", 185.0, 230.0);
        Form wrongType = form(3L, true, "부산중", 190.0, 240.0);
        ReflectionTestUtils.setField(wrongType, "type", FormType.MEISTER_TALENT);
        given(formRepository.findByStatus(FormStatus.SUBMITTED)).willReturn(List.of(first, second, wrongType,
                form(4L, false, "부산중", 190.0, 240.0),
                form(5L, true, "서울중", 190.0, 240.0),
                form(6L, true, "부산중", 170.0, 240.0),
                form(7L, true, "부산중", 190.0, 300.0)));

        assertIds(FormListRequest.builder().status(FormStatus.SUBMITTED).type(FormType.REGULAR)
                .mentoringProgram(true).schoolName("부산").firstRoundScoreMin(180.0)
                .firstRoundScoreMax(190.0).totalScoreMin(200.0).totalScoreMax(250.0)
                .sort("first-round-score-desc").build(), 2L, 1L);
        verify(formRepository).findByStatus(FormStatus.SUBMITTED);
    }

    @Test
    void 조건에_맞는_원서가_없으면_빈_목록을_반환한다() {
        given(formRepository.findByStatus(null)).willReturn(List.of());
        assertIds(FormListRequest.builder().mentoringProgram(true).build());
    }

    private void assertIds(FormListRequest request, Long... ids) {
        assertThat(useCase.execute(request)).extracting(FormSimpleResponse::getId).containsExactly(ids);
    }

    private Form form(Long id, boolean mentoring, String schoolName, Double firstScore, Double totalScore) {
        Form form = FormFixture.createForm(FormType.REGULAR);
        ReflectionTestUtils.setField(form, "id", id);
        form.assignExaminationNumber(id);
        ReflectionTestUtils.setField(form.getGrade(), "mentoringProgram", mentoring);
        ReflectionTestUtils.setField(form.getEducation().getSchool(), "name", schoolName);
        form.updateScore(new Score(0.0, null, 0, 0, 0, null, null, null, firstScore, totalScore));
        return form;
    }
}
