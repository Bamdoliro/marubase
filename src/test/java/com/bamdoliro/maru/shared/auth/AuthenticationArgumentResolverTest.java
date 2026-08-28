package com.bamdoliro.maru.shared.auth;

import com.bamdoliro.maru.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationArgumentResolverTest {

    @InjectMocks
    private AuthenticationArgumentResolver authenticationArgumentResolver;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private AuthenticationPrincipal authenticationPrincipal;

    @Mock
    private User user;

    @Test
    void 인증된_사용자를_엔티티가_아닌_인증_정보로_반환한다() {
        given(authenticationService.getCurrentUser(webRequest)).willReturn(user);
        given(methodParameter.getParameterAnnotation(AuthenticationPrincipal.class))
                .willReturn(authenticationPrincipal);
        given(authenticationPrincipal.authority()).willReturn(Authority.ADMIN);
        given(user.getAuthority())
                .willReturn(com.bamdoliro.maru.domain.user.domain.type.Authority.ADMIN);
        given(user.getId()).willReturn(1L);
        given(methodParameter.getParameterType()).willAnswer(
                invocation -> AuthenticatedUser.class
        );

        Object result = authenticationArgumentResolver.resolveArgument(
                methodParameter,
                null,
                webRequest,
                null
        );

        assertThat(result).isEqualTo(new AuthenticatedUser(1L));
    }
}
