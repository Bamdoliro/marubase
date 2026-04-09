package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.domain.auth.exception.AuthorityMismatchException;
import com.bamdoliro.maru.domain.form.exception.FormNotFoundException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.FormFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FormControllerTest extends RestDocsTestSupport {

    @Test
    void 원서를_상세_조회한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUseCase.execute(user, formId)).willReturn(FormFixture.createFormResponse());

        mockMvc.perform(get("/forms/{form-id}", formId)
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("accessToken")
                                        .description("이것은.엑세스.토큰")
                        ),
                        pathParameters(
                                parameterWithName("form-id").description("조회할 원서의 id")
                        )
                ));

        verify(queryFormUseCase, times(1)).execute(user, formId);
    }

    @Test
    void 원서를_상세_조회할_때_원서가_없으면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUseCase.execute(user, formId)).willThrow(new FormNotFoundException());

        mockMvc.perform(get("/forms/{form-id}", formId)
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(restDocs.document());
    }

    @Test
    void 원서를_상세_조회할_때_본인의_원서가_아니면_에러가_발생한다() throws Exception {
        Long formId = 1L;
        User user = UserFixture.createAdminUser();

        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(user);
        given(queryFormUseCase.execute(user, formId)).willThrow(new AuthorityMismatchException());

        mockMvc.perform(get("/forms/{form-id}", formId)
                        .cookie(AuthFixture.createAuthCookie())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(restDocs.document());
    }
}