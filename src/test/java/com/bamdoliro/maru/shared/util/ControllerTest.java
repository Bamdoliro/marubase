package com.bamdoliro.maru.shared.util;

import com.bamdoliro.maru.application.auth.LogInUseCase;
import com.bamdoliro.maru.application.auth.RefreshTokenUseCase;
import com.bamdoliro.maru.application.form.SubmitFormUseCase;
import com.bamdoliro.maru.application.school.SearchSchoolUseCase;
import com.bamdoliro.maru.application.user.SendEmailVerificationUseCase;
import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.domain.auth.service.TokenService;
import com.bamdoliro.maru.infrastructure.mail.SendEmailService;
import com.bamdoliro.maru.infrastructure.neis.SearchSchoolService;
import com.bamdoliro.maru.presentation.auth.AuthController;
import com.bamdoliro.maru.presentation.form.FormController;
import com.bamdoliro.maru.presentation.school.SchoolController;
import com.bamdoliro.maru.presentation.user.UserController;
import com.bamdoliro.maru.shared.config.properties.JwtProperties;
import com.bamdoliro.maru.shared.response.SharedController;
import com.bamdoliro.maru.shared.security.SecurityConfig;
import com.bamdoliro.maru.shared.security.auth.AuthDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Disabled
@Import(SecurityConfig.class)
@WebMvcTest({
        UserController.class,
        AuthController.class,
        SharedController.class,
        SchoolController.class,
        FormController.class
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @MockBean
    protected SignUpUserUseCase signUpUserUseCase;

    @MockBean
    protected LogInUseCase logInUseCase;

    @MockBean
    protected RefreshTokenUseCase refreshTokenUseCase;

    @MockBean
    protected SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @MockBean
    protected SearchSchoolUseCase searchSchoolUseCase;

    @MockBean
    protected SubmitFormUseCase submitFormUseCase;


    @MockBean
    protected TokenService tokenService;

    @MockBean
    protected AuthDetailsService authDetailsService;

    @MockBean
    protected SearchSchoolService searchSchoolService;

    @MockBean
    protected SendEmailService sendEmailService;


    @MockBean
    protected JwtProperties jwtProperties;


    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}