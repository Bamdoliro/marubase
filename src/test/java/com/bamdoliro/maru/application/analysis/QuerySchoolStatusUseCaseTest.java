package com.bamdoliro.maru.application.analysis;

import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.infrastructure.persistence.form.FormRepository;
import com.bamdoliro.maru.infrastructure.persistence.form.vo.SchoolStatusVo;
import com.bamdoliro.maru.presentation.analysis.dto.request.SchoolStatusRequest;
import com.bamdoliro.maru.presentation.analysis.dto.response.SchoolStatusResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuerySchoolStatusUseCaseTest {

    @InjectMocks
    private QuerySchoolStatusUseCase querySchoolStatusUseCase;

    @Mock
    private FormRepository formRepository;

    @Test
    void 어드민이_1차_합격자_중_타지역_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "비전중학교", "경기도 비전시 비전구 비전로 1"),
                new SchoolStatusVo("김이로", "밤돌중학교", "경기도 밤돌시 밤돌구 밤돌로 1"),
                new SchoolStatusVo("금곰돌", "곰돌중학교", "경기도 곰돌시 곰돌구 곰돌로 1"),
                new SchoolStatusVo("박이로", "마루중학교", "경기도 밤돌시 이로구 밤돌이로 1")
        );
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findNotBusanSchool(round)).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, false, null);

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findNotBusanSchool(round);
    }

    @Test
    void 어드민이_1차_합격자_중_부산_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8"),
                new SchoolStatusVo("김이로", "가락중학교", "부산광역시 강서구 가락대로 1405"),
                new SchoolStatusVo("금곰돌", "성동중학교", "부산광역시 남구 남동천로 38"),
                new SchoolStatusVo("박이로", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8")
        );
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findSchoolByAddress(round, "부산광역시")).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, true, null);

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findSchoolByAddress(round, "부산광역시");
    }

    @Test
    void 어드민이_1차_합격자_중_부산_특정구_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8"),
                new SchoolStatusVo("박이로", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8")
        );
        List<FormStatus> round = List.of(FormStatus.FIRST_PASSED, FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findSchoolByAddress(round, "부산광역시 사상구")).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, true, "사상구");

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findSchoolByAddress(round, "부산광역시 사상구");
    }

    @Test
    void 어드민이_2차_전형자_중_타지역_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "비전중학교", "경기도 비전시 비전구 비전로 1"),
                new SchoolStatusVo("김이로", "밤돌중학교", "경기도 밤돌시 밤돌구 밤돌로 1"),
                new SchoolStatusVo("금곰돌", "곰돌중학교", "경기도 곰돌시 곰돌구 곰돌로 1"),
                new SchoolStatusVo("박이로", "마루중학교", "경기도 밤돌시 이로구 밤돌이로 1")
        );
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findNotBusanSchool(round)).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, false, null);

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findNotBusanSchool(round);
    }

    @Test
    void 어드민이_2차_전형자_중_부산_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8"),
                new SchoolStatusVo("김이로", "가락중학교", "부산광역시 강서구 가락대로 1405"),
                new SchoolStatusVo("금곰돌", "성동중학교", "부산광역시 남구 남동천로 38"),
                new SchoolStatusVo("박이로", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8")
        );
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findSchoolByAddress(round, "부산광역시")).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, true, null);

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findSchoolByAddress(round, "부산광역시");
    }

    @Test
    void 어드민이_2차_전형자_중_부산_특정구_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8"),
                new SchoolStatusVo("박이로", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8")
        );
        List<FormStatus> round = List.of(FormStatus.FAILED, FormStatus.PASSED);
        given(formRepository.findSchoolByAddress(round, "부산광역시 사상구")).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, true, "사상구");

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findSchoolByAddress(round, "부산광역시 사상구");
    }

    @Test
    void 어드민이_최종_합격자_중_타지역_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "비전중학교", "경기도 비전시 비전구 비전로 1"),
                new SchoolStatusVo("김이로", "밤돌중학교", "경기도 밤돌시 밤돌구 밤돌로 1"),
                new SchoolStatusVo("금곰돌", "곰돌중학교", "경기도 곰돌시 곰돌구 곰돌로 1"),
                new SchoolStatusVo("박이로", "마루중학교", "경기도 밤돌시 이로구 밤돌이로 1")
        );
        List<FormStatus> round = List.of(FormStatus.PASSED);
        given(formRepository.findNotBusanSchool(round)).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, false, null);

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findNotBusanSchool(round);
    }

    @Test
    void 어드민이_최종_합격자_중_부산_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8"),
                new SchoolStatusVo("김이로", "가락중학교", "부산광역시 강서구 가락대로 1405"),
                new SchoolStatusVo("금곰돌", "성동중학교", "부산광역시 남구 남동천로 38"),
                new SchoolStatusVo("박이로", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8")
        );
        List<FormStatus> round = List.of(FormStatus.PASSED);
        given(formRepository.findSchoolByAddress(round, "부산광역시")).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, true, null);

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findSchoolByAddress(round, "부산광역시");
    }

    @Test
    void 어드민이_최종_합격자_중_부산_특정구_출신을_조회한다() {
        // given
        List<SchoolStatusVo> voList = List.of(
                new SchoolStatusVo("김밤돌", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8"),
                new SchoolStatusVo("박이로", "신라중학교", "부산광역시 사상구 백양대로700번길 35-8")
        );
        List<FormStatus> round = List.of(FormStatus.PASSED);
        given(formRepository.findSchoolByAddress(round, "부산광역시 사상구")).willReturn(voList);
        SchoolStatusRequest request = new SchoolStatusRequest(round, true, "사상구");

        // when
        List<SchoolStatusResponse> responseList = querySchoolStatusUseCase.execute(request);

        // then
        Assertions.assertEquals(responseList.size(), voList.size());
        verify(formRepository, times(1)).findSchoolByAddress(round, "부산광역시 사상구");
    }
}