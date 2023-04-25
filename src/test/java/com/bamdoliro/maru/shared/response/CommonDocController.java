package com.bamdoliro.maru.shared.response;

import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.shared.property.EnumProperty;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@PermitAll
@RequestMapping("/common")
@RestController
public class CommonDocController {

    @GetMapping("/enum")
    public EnumDocs findEnums() {
        return EnumDocs.builder()
                .authority(getDocs(Authority.values()))
                .build();
    }

    private Map<String, String> getDocs(EnumProperty[] property) {
        return Arrays.stream(property)
                .collect(Collectors.toMap(EnumProperty::name, EnumProperty::getDescription));
    }
}