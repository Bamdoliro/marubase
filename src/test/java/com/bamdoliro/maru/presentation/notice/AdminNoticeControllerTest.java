package com.bamdoliro.maru.presentation.notice;

import com.bamdoliro.maru.domain.notice.exception.NoticeNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.dto.request.FileMetadata;
import com.bamdoliro.maru.infrastructure.s3.exception.FileCountLimitExceededException;
import com.bamdoliro.maru.presentation.notice.dto.request.NoticeRequest;
import com.bamdoliro.maru.presentation.notice.dto.response.UploadFileResponse;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.SharedFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Collections;
import java.util.List;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminNoticeControllerTest extends RestDocsTestSupport {

    @Test
    void 공지사항을_생성한다() throws Exception {
        NoticeRequest request = new NoticeRequest("오늘 급식 맛있엇나용?", "토요일인데요", List.of("notice-file.pdf", "notice-file.hwp"));
        given(createNoticeUseCase.execute(any(NoticeRequest.class))).willReturn(SharedFixture.createIdResponse());

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(post("/admin/notices")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                )

                .andExpect(status().isCreated())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("64글자 이내의 제목"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("1024글자 이내의 내용"),
                                fieldWithPath("fileNameList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("파일 이름 목록 (파일이 없는 경우 null)")
                        )
                ));

        verify(createNoticeUseCase, times(1)).execute(any(NoticeRequest.class));
    }

    @Test
    void 공지사항을_수정한다() throws Exception {
        Long id = 1L;
        NoticeRequest request = new NoticeRequest("이거 맞나", "아님 말고...", List.of("notice-file.pdf", "notice-file.hwp"));
        willDoNothing().given(updateNoticeUseCase).execute(id, request);

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(put("/admin/notices/{notice-id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("notice-id")
                                        .description("공지사항 id")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("64글자 이내의 제목"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("1024글자 이내의 내용"),
                                fieldWithPath("fileNameList")
                                        .type(JsonFieldType.ARRAY)
                                        .description("파일 이름 목록 (파일이 없는 경우 null)")
                        )
                ));
    }

    @Test
    void 공지사항을_수정할_때_공지사항이_없으면_에러가_발생한다() throws Exception {
        Long id = 1L;
        NoticeRequest request = new NoticeRequest("이거 맞나", "아님 말고...", List.of("notice-file.pdf", "notice-file.hwp"));
        willThrow(new NoticeNotFoundException()).given(updateNoticeUseCase).execute(eq(1L), any(NoticeRequest.class));

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(put("/admin/notices/{notice-id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))

                .andExpect(status().isNotFound())

                .andDo(restDocs.document());
    }

    @Test
    void 공지사항을_삭제한다() throws Exception {
        Long id = 1L;
        willDoNothing().given(deleteNoticeUseCase).execute(id);

        User user = UserFixture.createAdminUser();
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);

        mockMvc.perform(delete("/admin/notices/{notice-id}", id)
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        pathParameters(
                                parameterWithName("notice-id")
                                        .description("공지사항 id")
                        )
                ));

        verify(deleteNoticeUseCase, times(1)).execute(id);
    }

    @Test
    void 공지사항_파일을_업로드한다() throws Exception {
        List<FileMetadata> metadataList = Collections.nCopies(2, new FileMetadata(
                "notice-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                10 * MB
        ));
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadFileUseCase.execute(anyList())).willReturn(List.of(
                new UploadFileResponse(
                        SharedFixture.createNoticeFileUrlResponse(),
                        "notice-file.pdf"
                ),
                new UploadFileResponse(
                        SharedFixture.createNoticeFileUrlResponse(),
                        "notice-file.pdf"
                )
        ));

        mockMvc.perform(post("/admin/notices/files")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadataList))
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        ),
                        requestFields(
                                fieldWithPath("[].fileName")
                                        .description("파일 이름"),
                                fieldWithPath("[].mediaType")
                                        .description("미디어 타입"),
                                fieldWithPath("[].fileSize")
                                        .description("파일 용량")
                        )
                ));

        verify(uploadFileUseCase, times(1)).execute(anyList());
    }

    @Test
    void 공지사항_파일을_4개_이상_업로드하면_에러가_발생한다() throws Exception {
        List<FileMetadata> metadataList = Collections.nCopies(4, new FileMetadata(
                "notice-file.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                10 * MB
        ));
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(uploadFileUseCase.execute(anyList())).willThrow(new FileCountLimitExceededException(3));

        mockMvc.perform(post("/admin/notices/files")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(metadataList))
                )
                .andExpect(status().isBadRequest())
                .andDo(restDocs.document());

        verify(uploadFileUseCase, times(1)).execute(anyList());
    }

}
