package com.bamdoliro.maru.shared.response;

import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.shared.fixture.AuthFixture;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import com.bamdoliro.maru.shared.security.auth.AuthDetails;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtControllerTest extends RestDocsTestSupport {

    @Test
    void 인증에_성공한다() throws Exception {
        User user = UserFixture.createUser();
        String token = AuthFixture.createAccessTokenString();
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        given(tokenService.getEmail(anyString())).willReturn(user.getEmail());
        given(authDetailsService.loadUserByUsername(user.getEmail())).willReturn(new AuthDetails(user));

        mockMvc.perform(get("/shared/jwt")
                        .header(HttpHeaders.AUTHORIZATION, jwtProperties.getPrefix() + " " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer token")
                        )
                ));

        verify(tokenService, times(1)).getEmail(anyString());
        verify(authDetailsService, times(1)).loadUserByUsername(user.getEmail());
    }

    @Test
    void 토큰이_만료되어_인증에_실패한다() throws Exception {
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        doThrow(new ExpiredTokenException()).when(tokenService).getEmail(anyString());

        mockMvc.perform(get("/shared/jwt")
                        .header(HttpHeaders.AUTHORIZATION, jwtProperties.getPrefix() + " " + AuthFixture.createAccessTokenString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andDo(restDocs.document());

        verify(authDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void 유효하지_않은_토큰을_전달하면_인증에_실패한다() throws Exception {
        given(jwtProperties.getPrefix()).willReturn("Bearer");
        doThrow(new InvalidTokenException()).when(tokenService).getEmail(anyString());

        mockMvc.perform(get("/shared/jwt")
                        .header(HttpHeaders.AUTHORIZATION, jwtProperties.getPrefix() + " 이것은.이상한.토큰")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andDo(restDocs.document());

        verify(authDetailsService, never()).loadUserByUsername(anyString());
    }
}
