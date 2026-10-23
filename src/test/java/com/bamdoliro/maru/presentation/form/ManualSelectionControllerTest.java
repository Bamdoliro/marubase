package com.bamdoliro.maru.presentation.form;

import com.bamdoliro.maru.application.form.PassOrFailFormUseCase;
import com.bamdoliro.maru.application.schedule.ExecuteFirstPassSelectionUseCase;
import com.bamdoliro.maru.application.schedule.ExecuteSecondPassSelectionUseCase;
import com.bamdoliro.maru.domain.auth.exception.EmptyTokenException;
import com.bamdoliro.maru.domain.form.exception.InvalidFormStatusException;
import com.bamdoliro.maru.domain.form.exception.NoSelectionCandidatesException;
import com.bamdoliro.maru.domain.schedule.exception.FirstPassAlreadyExecutedException;
import com.bamdoliro.maru.shared.auth.AuthenticationArgumentResolver;
import com.bamdoliro.maru.shared.auth.AuthenticationService;
import com.bamdoliro.maru.shared.error.GlobalExceptionHandler;
import com.bamdoliro.maru.shared.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ManualSelectionControllerTest {

    @Mock private AuthenticationService authenticationService;
    @Mock private ExecuteFirstPassSelectionUseCase firstSelection;
    @Mock private ExecuteSecondPassSelectionUseCase secondSelection;
    @Mock private PassOrFailFormUseCase results;
    @InjectMocks private AdminFormController controller;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationArgumentResolver(authenticationService))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void 관리자가_본문없이_일차를_일괄선발한다() throws Exception {
        admin();
        mvc.perform(patch("/admin/forms/first-round/select")).andExpect(status().isNoContent());
        then(firstSelection).should().execute();
    }

    @Test
    void 기존_이차_경로는_보호된_실행기를_사용한다() throws Exception {
        admin();
        mvc.perform(patch("/admin/forms/second-round/select")).andExpect(status().isNoContent());
        then(secondSelection).should().execute();
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round/select", "second-round/select", "second-round/result"})
    void 일반사용자는_실행할_수_없다(String path) throws Exception {
        given(authenticationService.getCurrentUser(any())).willReturn(UserFixture.createUser());
        mvc.perform(patch("/admin/forms/" + path)).andExpect(status().isForbidden());
        then(firstSelection).shouldHaveNoInteractions();
        then(secondSelection).shouldHaveNoInteractions();
        then(results).shouldHaveNoInteractions();
    }

    @ParameterizedTest
    @ValueSource(strings = {"first-round/select", "second-round/select", "second-round/result"})
    void 비인증_요청은_실행할_수_없다(String path) throws Exception {
        given(authenticationService.getCurrentUser(any())).willThrow(new EmptyTokenException());
        mvc.perform(patch("/admin/forms/" + path)).andExpect(status().isUnauthorized());
        then(firstSelection).shouldHaveNoInteractions();
        then(secondSelection).shouldHaveNoInteractions();
        then(results).shouldHaveNoInteractions();
    }

    @Test
    void 재실행은_409와_명확한_코드를_반환한다() throws Exception {
        admin();
        willThrow(new FirstPassAlreadyExecutedException()).given(firstSelection).execute();
        mvc.perform(patch("/admin/forms/first-round/select"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("FIRST_PASS_ALREADY_EXECUTED"));
    }

    @Test
    void 대상이_없으면_409를_반환한다() throws Exception {
        admin();
        willThrow(new NoSelectionCandidatesException()).given(secondSelection).execute();
        mvc.perform(patch("/admin/forms/second-round/select"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("NO_SELECTION_CANDIDATES"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"formList\":[{\"formId\":1}]}",
            "{\"formList\":[{\"formId\":1,\"pass\":null}]}", "{\"formList\":[null]}", "{\"formList\":[]}"})
    void 누락된_결과나_빈목록을_400으로_거부한다(String body) throws Exception {
        admin();
        mvc.perform(patch("/admin/forms/second-round/result")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        then(results).shouldHaveNoInteractions();
    }

    @Test
    void 미승인_결과_변경은_409를_반환한다() throws Exception {
        admin();
        willThrow(new InvalidFormStatusException()).given(results).execute(any());
        mvc.perform(patch("/admin/forms/second-round/result").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formList\":[{\"formId\":1,\"pass\":true}]}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("INVALID_FORM_STATUS"));
    }

    private void admin() {
        given(authenticationService.getCurrentUser(any())).willReturn(UserFixture.createAdminUser());
    }
}
