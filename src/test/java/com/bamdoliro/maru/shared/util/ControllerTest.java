package com.bamdoliro.maru.shared.util;

import com.bamdoliro.maru.application.user.SignUpUserUseCase;
import com.bamdoliro.maru.shared.security.SecurityConfig;
import com.bamdoliro.maru.presentation.user.UserController;
import com.bamdoliro.maru.shared.response.CommonDocController;
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
        CommonDocController.class
})
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected SignUpUserUseCase signUpUserUseCase;


    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}