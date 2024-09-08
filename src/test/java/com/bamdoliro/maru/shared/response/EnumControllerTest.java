package com.bamdoliro.maru.shared.response;

import com.bamdoliro.maru.shared.util.CustomResponseFieldsSnippet;
import com.bamdoliro.maru.shared.util.RestDocsTestSupport;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EnumControllerTest extends RestDocsTestSupport {

    @Test
    void enums() throws Exception {
        ResultActions result = this.mockMvc.perform(
                get("/shared/enum")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        MvcResult mvcResult = result.andReturn();
        EnumDocs enumDocs = getData(mvcResult);

        result.andExpect(status().isOk())
                .andDo(restDocs.document(
                        customResponseFields("custom-response",
                                beneathPath("authority").withSubsectionId("authority"),
                                attributes(key("title").value("Authority")),
                                enumConvertFieldDescriptor((enumDocs.getAuthority()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("achievementLevel").withSubsectionId("achievementLevel"),
                                attributes(key("title").value("AchievementLevel")),
                                enumConvertFieldDescriptor((enumDocs.getAchievementLevel()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("certificate").withSubsectionId("certificate"),
                                attributes(key("title").value("Certificate")),
                                enumConvertFieldDescriptor((enumDocs.getCertificate()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("formStatus").withSubsectionId("formStatus"),
                                attributes(key("title").value("FormStatus")),
                                enumConvertFieldDescriptor((enumDocs.getFormStatus()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("formType").withSubsectionId("formType"),
                                attributes(key("title").value("FormType")),
                                enumConvertFieldDescriptor((enumDocs.getFormType()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("formCategory").withSubsectionId("formCategory"),
                                attributes(key("title").value("FormCategory")),
                                enumConvertFieldDescriptor((enumDocs.getFormCategory()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("gender").withSubsectionId("gender"),
                                attributes(key("title").value("Gender")),
                                enumConvertFieldDescriptor((enumDocs.getGender()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("graduationType").withSubsectionId("graduationType"),
                                attributes(key("title").value("GraduationType")),
                                enumConvertFieldDescriptor((enumDocs.getGraduationType()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("fairStatus").withSubsectionId("fairStatus"),
                                attributes(key("title").value("FairStatus")),
                                enumConvertFieldDescriptor((enumDocs.getFairStatus()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("fairType").withSubsectionId("fairType"),
                                attributes(key("title").value("FairType")),
                                enumConvertFieldDescriptor((enumDocs.getFairType()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("questionCategory").withSubsectionId("questionCategory"),
                                attributes(key("title").value("QuestionCategory")),
                                enumConvertFieldDescriptor((enumDocs.getQuestionCategory()))
                        ),
                        customResponseFields("custom-response",
                                beneathPath("verificationType").withSubsectionId("verificationType"),
                                attributes(key("title").value("VerificationType")),
                                enumConvertFieldDescriptor((enumDocs.getVerificationType()))
                        )
                ));
    }

    public static CustomResponseFieldsSnippet customResponseFields
            (String type,
             PayloadSubsectionExtractor<?> subsectionExtractor,
             Map<String, Object> attributes, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes
                , true);
    }

    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);
    }

    private EnumDocs getData(MvcResult result) throws IOException {
        return objectMapper
                .readValue(result.getResponse().getContentAsByteArray(),
                        new TypeReference<>() {
                        }
                );
    }
}
