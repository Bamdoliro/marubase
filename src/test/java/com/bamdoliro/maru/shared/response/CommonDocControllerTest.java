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

public class CommonDocControllerTest extends RestDocsTestSupport {

    @Test
    public void enums() throws Exception {
        ResultActions result = this.mockMvc.perform(
                get("/common/enum")
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
                        new TypeReference<EnumDocs>() {}
                );
    }
}
