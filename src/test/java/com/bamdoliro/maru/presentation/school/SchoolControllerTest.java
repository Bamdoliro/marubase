package com.bamdoliro.maru.presentation.school;

import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.SchoolFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchoolControllerTest extends RestDocsTestSupport {

    @Test
    void 학교를_검색한다() throws Exception {
        given(searchSchoolUseCase.execute(anyString())).willReturn(SchoolFixture.createSchoolListResponse());

        mockMvc.perform(get("/schools")
                        .param("q", "부산소프트")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.액세스.토큰")
                        ),
                        queryParameters(
                                parameterWithName("q")
                                        .description("검색할 학교 이름")
                        )
                ));
    }

    @Test
    void 학교를_검색할_때_결과가_10개_이상이라면_상위_10개만_반환한다() throws Exception {
        given(searchSchoolUseCase.execute(anyString())).willReturn(SchoolFixture.createSchoolMaxListResponse());


        mockMvc.perform(get("/schools")
                        .param("q", "비전")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document());
    }

    @Test
    void 학교를_검색할_때_결과가_없다면_빈_리스트를_반환한다() throws Exception {
        given(searchSchoolUseCase.execute(anyString())).willReturn(List.of());


        mockMvc.perform(get("/schools")
                        .param("q", "누가봐도없을것같은검색어")
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document());
    }
}