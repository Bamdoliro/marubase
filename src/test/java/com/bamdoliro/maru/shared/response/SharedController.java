package com.bamdoliro.maru.shared.response;

import com.bamdoliro.maru.domain.form.domain.type.AchievementLevel;
import com.bamdoliro.maru.domain.form.domain.type.Certificate;
import com.bamdoliro.maru.domain.form.domain.type.FormStatus;
import com.bamdoliro.maru.domain.form.domain.type.FormType;
import com.bamdoliro.maru.domain.form.domain.type.Gender;
import com.bamdoliro.maru.domain.form.domain.type.GraduationType;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.shared.property.EnumProperty;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/shared")
@RestController
public class SharedController {

    @PermitAll
    @GetMapping("/enum")
    public EnumDocs findEnums() {
        return EnumDocs.builder()
                .authority(getDocs(Authority.values()))
                .achievementLevel(getDocs(AchievementLevel.values()))
                .certificate(getDocs(Certificate.values()))
                .formStatus(getDocs(FormStatus.values()))
                .formType(getDocs(FormType.values()))
                .gender(getDocs(GraduationType.values()))
                .graduationType(getDocs(GraduationType.values()))
                .build();
    }

    private Map<String, String> getDocs(EnumProperty[] property) {
        return Arrays.stream(property)
                .collect(Collectors.toMap(EnumProperty::name, EnumProperty::getDescription));
    }

    @GetMapping("/jwt")
    public String jwtTest() {
        return "success";
    }
}