package com.bamdoliro.maru.presentation.fair;

import com.bamdoliro.maru.domain.fair.domain.type.FairType;
import com.bamdoliro.maru.domain.fair.exception.FairNotFoundException;
import com.bamdoliro.maru.domain.fair.exception.HeadcountExceededException;
import com.bamdoliro.maru.domain.fair.exception.NotApplicationPeriodException;
import com.bamdoliro.maru.presentation.fair.dto.request.AttendAdmissionFairRequest;
import com.bamdoliro.maru.shared.fixture.FairFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FairControllerTest extends RestDocsTestSupport {

    @Test
    void 입학설명회에_참가_신청을_한다() throws Exception {
        Long fairId = 1L;
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();
        willDoNothing().given(attendAdmissionFairUseCase).execute(any(Long.class), any(AttendAdmissionFairRequest.class));

        mockMvc.perform(post("/fairs/{fair-id}", fairId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("fair-id")
                                        .description("입학설명회 id")
                        ),
                        requestFields(
                                fieldWithPath("schoolName")
                                        .type(JsonFieldType.STRING)
                                        .description("신청자 학교 이름"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("신청자 이름"),
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("신청자 유형 (학생, 학부모, 교사, 기타등등...)"),
                                fieldWithPath("phoneNumber")
                                        .type(JsonFieldType.STRING)
                                        .description("신청자 전화번호"),
                                fieldWithPath("headcount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("신청자 인원수"),
                                fieldWithPath("question")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("신청자 질문사항")
                        )
                ));

        verify(attendAdmissionFairUseCase, times(1)).execute(any(Long.class), any(AttendAdmissionFairRequest.class));
    }

    @Test
    void 입학설명회에_참가_신청을_할_때_해당_설명회가_없다면_에러가_발생한다() throws Exception {
        Long fairId = 1L;
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();
        doThrow(new FairNotFoundException()).when(attendAdmissionFairUseCase).execute(any(Long.class), any(AttendAdmissionFairRequest.class));

        mockMvc.perform(post("/fairs/{fair-id}", fairId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());

        verify(attendAdmissionFairUseCase, times(1)).execute(any(Long.class), any(AttendAdmissionFairRequest.class));
    }

    @Test
    void 입학설명회에_참가_신청을_할_때_인원_수를_초과했으면_에러가_발생한다() throws Exception {
        Long fairId = 1L;
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();
        doThrow(new HeadcountExceededException()).when(attendAdmissionFairUseCase).execute(any(Long.class), any(AttendAdmissionFairRequest.class));

        mockMvc.perform(post("/fairs/{fair-id}", fairId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isConflict())

                .andDo(restDocs.document());

        verify(attendAdmissionFairUseCase, times(1)).execute(any(Long.class), any(AttendAdmissionFairRequest.class));
    }

    @Test
    void 입학설명회에_참가_신청을_할_때_신청_기간이_아니라면_에러가_발생한다() throws Exception {
        Long fairId = 1L;
        AttendAdmissionFairRequest request = FairFixture.createAttendAdmissionFairRequest();
        doThrow(new NotApplicationPeriodException()).when(attendAdmissionFairUseCase).execute(any(Long.class), any(AttendAdmissionFairRequest.class));

        mockMvc.perform(post("/fairs/{fair-id}", fairId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isConflict())

                .andDo(restDocs.document());

        verify(attendAdmissionFairUseCase, times(1)).execute(any(Long.class), any(AttendAdmissionFairRequest.class));
    }

    @Test
    void 입학설명회_일정을_불러온다() throws Exception {
        given(queryFairListUseCase.execute(any(FairType.class))).willReturn(FairFixture.createFairResponseList());

        mockMvc.perform(get("/fairs")
                        .param("type", FairType.STUDENT_AND_PARENT.name())
                        .accept(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("type")
                                        .optional()
                                        .description("<<fair-type,입학설명회 유형 (미지정시 전체 조회)>>")
                        )
                ));

        verify(queryFairListUseCase, times(1)).execute(any(FairType.class));
    }

}