package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.Form;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.presentation.analysis.dto.request.GenderRatioRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.GenderRatioResponse;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueryGenderRatioUseCaseTest {

    @InjectMocks
    private QueryGenderRatioUseCase queryGenderRatioUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 어드민이_1차_합격자_중_일반_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createMaleForm(FormType.REGULAR),
                FormFixture.createMaleForm(FormType.REGULAR)
        );
        formList.forEach(Form::firstPass);

        given(formRepository.findByCategory(FormType.Category.REGULAR)).willReturn(formList);
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.REGULAR, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 1L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.REGULAR);
    }

    @Test
    void 어드민이_1차_합격자_중_특별_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.NATIONAL_BASIC_LIVING),
                FormFixture.createMaleForm(FormType.MEISTER_TALENT),
                FormFixture.createMaleForm(FormType.MULTICULTURAL),
                FormFixture.createMaleForm(FormType.FARMING_AND_FISHING),
                FormFixture.createMaleForm(FormType.ONE_PARENT),
                FormFixture.createMaleForm(FormType.NEAR_POVERTY)
        );
        formList.forEach(Form::firstPass);

        given(formRepository.findByCategory(FormType.Category.MEISTER_TALENT))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.MEISTER_TALENT))
                        .toList());
        given(formRepository.findByCategory(FormType.Category.SOCIAL_INTEGRATION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.SOCIAL_INTEGRATION))
                        .toList());
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.SPECIAL, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 5L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.MEISTER_TALENT);
        verify(formRepository, times(1)).findByCategory(FormType.Category.SOCIAL_INTEGRATION);
    }

    @Test
    void 어드민이_1차_합격자_중_정원_외_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.NATIONAL_VETERANS_EDUCATION),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createMaleForm(FormType.NATIONAL_VETERANS_EDUCATION),
                FormFixture.createMaleForm(FormType.SPECIAL_ADMISSION)
        );
        formList.forEach(Form::firstPass);

        given(formRepository.findByCategory(FormType.Category.NATIONAL_VETERANS_EDUCATION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.NATIONAL_VETERANS_EDUCATION))
                        .toList());
        given(formRepository.findByCategory(FormType.Category.SPECIAL_ADMISSION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.SPECIAL_ADMISSION))
                        .toList());
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.SUPERNUMERARY, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 2L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.NATIONAL_VETERANS_EDUCATION);
        verify(formRepository, times(1)).findByCategory(FormType.Category.SPECIAL_ADMISSION);
    }

    @Test
    void 어드민이_2차_전형자_중_일반_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createMaleForm(FormType.REGULAR),
                FormFixture.createMaleForm(FormType.REGULAR)
        );
        formList.forEach(Form::fail);

        given(formRepository.findByCategory(FormType.Category.REGULAR)).willReturn(formList);
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.REGULAR, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 1L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.REGULAR);
    }

    @Test
    void 어드민이_2차_전형자_중_특별_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.NATIONAL_BASIC_LIVING),
                FormFixture.createMaleForm(FormType.MEISTER_TALENT),
                FormFixture.createMaleForm(FormType.MULTICULTURAL),
                FormFixture.createMaleForm(FormType.FARMING_AND_FISHING),
                FormFixture.createMaleForm(FormType.ONE_PARENT),
                FormFixture.createMaleForm(FormType.NEAR_POVERTY)
        );
        formList.forEach(Form::fail);

        given(formRepository.findByCategory(FormType.Category.MEISTER_TALENT))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.MEISTER_TALENT))
                        .toList());
        given(formRepository.findByCategory(FormType.Category.SOCIAL_INTEGRATION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.SOCIAL_INTEGRATION))
                        .toList());
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.SPECIAL, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 5L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.MEISTER_TALENT);
        verify(formRepository, times(1)).findByCategory(FormType.Category.SOCIAL_INTEGRATION);
    }

    @Test
    void 어드민이_2차_전형자_중_정원_외_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.NATIONAL_VETERANS_EDUCATION),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createMaleForm(FormType.NATIONAL_VETERANS_EDUCATION),
                FormFixture.createMaleForm(FormType.SPECIAL_ADMISSION)
        );
        formList.forEach(Form::fail);

        given(formRepository.findByCategory(FormType.Category.NATIONAL_VETERANS_EDUCATION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.NATIONAL_VETERANS_EDUCATION))
                        .toList());
        given(formRepository.findByCategory(FormType.Category.SPECIAL_ADMISSION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.SPECIAL_ADMISSION))
                        .toList());
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.SUPERNUMERARY, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 2L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.NATIONAL_VETERANS_EDUCATION);
        verify(formRepository, times(1)).findByCategory(FormType.Category.SPECIAL_ADMISSION);
    }

    @Test
    void 어드민이_최종_합격자_중_일반_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.REGULAR),
                FormFixture.createMaleForm(FormType.REGULAR),
                FormFixture.createMaleForm(FormType.REGULAR)
        );
        formList.forEach(Form::pass);

        given(formRepository.findByCategory(FormType.Category.REGULAR)).willReturn(formList);
        List<FormStatus> round = List.of(FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.REGULAR, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 1L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.REGULAR);
    }

    @Test
    void 어드민이_최종_합격자_중_특별_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.MEISTER_TALENT),
                FormFixture.createForm(FormType.NATIONAL_BASIC_LIVING),
                FormFixture.createMaleForm(FormType.MEISTER_TALENT),
                FormFixture.createMaleForm(FormType.MULTICULTURAL),
                FormFixture.createMaleForm(FormType.FARMING_AND_FISHING),
                FormFixture.createMaleForm(FormType.ONE_PARENT),
                FormFixture.createMaleForm(FormType.NEAR_POVERTY)
        );
        formList.forEach(Form::pass);

        given(formRepository.findByCategory(FormType.Category.MEISTER_TALENT))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.MEISTER_TALENT))
                        .toList());
        given(formRepository.findByCategory(FormType.Category.SOCIAL_INTEGRATION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.SOCIAL_INTEGRATION))
                        .toList());
        List<FormStatus> round = List.of(FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.SPECIAL, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 5L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.MEISTER_TALENT);
        verify(formRepository, times(1)).findByCategory(FormType.Category.SOCIAL_INTEGRATION);
    }

    @Test
    void 어드민이_최종_합격자_중_정원_외_전형_성비를_조회한다() {
        // given
        List<Form> formList = List.of(
                FormFixture.createForm(FormType.NATIONAL_VETERANS_EDUCATION),
                FormFixture.createForm(FormType.SPECIAL_ADMISSION),
                FormFixture.createMaleForm(FormType.NATIONAL_VETERANS_EDUCATION),
                FormFixture.createMaleForm(FormType.SPECIAL_ADMISSION)
        );
        formList.forEach(Form::firstPass);

        given(formRepository.findByCategory(FormType.Category.NATIONAL_VETERANS_EDUCATION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.NATIONAL_VETERANS_EDUCATION))
                        .toList());
        given(formRepository.findByCategory(FormType.Category.SPECIAL_ADMISSION))
                .willReturn(formList.stream()
                        .filter(form -> form.getType().categoryEquals(FormType.Category.SPECIAL_ADMISSION))
                        .toList());
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        GenderRatioRequest request = new GenderRatioRequest(round, FormType.Category.SUPERNUMERARY, "CURRENT");

        // when
        List<GenderRatioResponse> responseList = queryGenderRatioUseCase.execute(request);

        // then
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanMale() + response.getOtherLocationMale())
                .sum(), 2L);
        assertEquals(responseList.stream()
                .mapToLong(response -> response.getBusanFemale() + response.getOtherLocationFemale())
                .sum(), 2L);
        verify(formRepository, times(1)).findByCategory(FormType.Category.NATIONAL_VETERANS_EDUCATION);
        verify(formRepository, times(1)).findByCategory(FormType.Category.SPECIAL_ADMISSION);
    }
}