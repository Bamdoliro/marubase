package com.bamdoliro.maru.presentation.school;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.SchoolFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchoolControllerTest extends RestDocsTestSupport {

    @Test
    void 학교를_검색한다() throws Exception {
        User user = UserFixture.createUser();
        given(searchSchoolUseCase.execute()).willReturn(SchoolFixture.createSchoolListResponse());
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getUser(anyString())).willReturn(user);


        mockMvc.perform(get("/school")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));
    }

    @Test
    void 학교를_검색할_때_결과가_10개_이상이라면_상위_10개만_반환한다() throws Exception {
        User user = UserFixture.createUser();
        given(searchSchoolUseCase.execute()).willReturn(SchoolFixture.createSchoolMaxListResponse());
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getUser(anyString())).willReturn(user);


        mockMvc.perform(get("/school")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document());
    }

    @Test
    void 학교를_검색할_때_결과가_없다면_빈_리스트를_반환한다() throws Exception {
        User user = UserFixture.createUser();
        given(searchSchoolUseCase.execute()).willReturn(List.of());
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getUser(anyString())).willReturn(user);


        mockMvc.perform(get("/school")
                        .header(HttpHeaders.AUTHORIZATION, AuthFixture.createAuthHeader())
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andDo(restDocs.document());
    }
}