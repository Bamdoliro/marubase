package com.bamdoliro.maru.presentation.policy;

import com.bamdoliro.maru.domain.policy.SelectionPolicy;
import com.bamdoliro.maru.domain.policy.TicketNotice;
import com.bamdoliro.maru.domain.policy.formula.ScoreFormulaPreview;
import com.bamdoliro.maru.presentation.policy.dto.PolicyRequest;
import com.bamdoliro.maru.presentation.policy.dto.PolicyResponse;
import com.bamdoliro.maru.presentation.policy.dto.ScorePreviewRequest;
import com.bamdoliro.maru.shared.auth.AuthenticatedUser;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAdmissionPolicyControllerTest extends RestDocsTestSupport {
    private void authenticate() throws Exception {
        given(authenticationArgumentResolver.supportsParameter(any(MethodParameter.class))).willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(new AuthenticatedUser(7L));
    }

    @Test
    void 관리자_정책_조회와_수정() throws Exception {
        authenticate();
        PolicyResponse response = new PolicyResponse(1L, 2027, 0L, false,
                SelectionPolicy.defaults(), TicketNotice.defaults());
        given(admissionPolicyService.current()).willReturn(response);
        mockMvc.perform(get("/admin/admission-policies/current"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.selection.regular").value(48));
        given(admissionPolicyService.update(eq(7L), any())).willReturn(response);
        mockMvc.perform(put("/admin/admission-policies/current").contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new PolicyRequest(1L, -1L, SelectionPolicy.defaults(), TicketNotice.defaults()))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.version").value(0));
        verify(admissionPolicyService).update(eq(7L), any());
    }

    @Test
    void 잘못된_정원은_서비스에_전달하지_않는다() throws Exception {
        authenticate();
        SelectionPolicy invalid = new SelectionPolicy(-1, 0, 0, 0, 0, BigDecimal.ONE, BigDecimal.ZERO);
        mockMvc.perform(put("/admin/admission-policies/current").contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new PolicyRequest(1L, -1L, invalid, TicketNotice.defaults()))))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(admissionPolicyService);
    }

    @Test
    void 점수식_미리보기_결과를_반환한다() throws Exception {
        authenticate();
        given(scoreFormulaPreview.execute(any(), any())).willReturn(
                new ScoreFormulaPreview.Result(true, Map.of("점수", BigDecimal.TEN), Map.of(), null));
        mockMvc.perform(post("/admin/admission-policies/score-preview").contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ScorePreviewRequest("출력 점수 = 10", Map.of()))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.outputs.점수").value(10));
        verifyNoInteractions(admissionPolicyService);
    }

    @Test
    void 누락된_정원을_0으로_취급하지_않는다() throws Exception {
        authenticate();
        String json = toJson(new PolicyRequest(1L, -1L, SelectionPolicy.defaults(), TicketNotice.defaults()));
        json = json.replace("\"regular\":48,", "");
        mockMvc.perform(put("/admin/admission-policies/current").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(admissionPolicyService);
    }
}
