package com.bamdoliro.maru.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumDocs {

    Map<String, String> authority;
    Map<String, String> achievementLevel;
    Map<String, String> certificate;
    Map<String, String> formStatus;
    Map<String, String> formType;
    Map<String, String> formCategory;
    Map<String, String> gender;
    Map<String, String> graduationType;
    Map<String, String> fairStatus;
    Map<String, String> fairType;
    Map<String, String> questionCategory;
    Map<String, String> verificationType;
}