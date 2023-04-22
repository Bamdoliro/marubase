package com.bamdoliro.maru.shared.response;

import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.shared.property.EnumProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/common")
public class CommonDocController {

    @GetMapping("/enum")
    public SingleCommonResponse<EnumDocs> findEnums() {
        return CommonResponse.ok(
                EnumDocs.builder()
                        .authority(getDocs(Authority.values()))
                        .build()
        );
    }

    private Map<String, String> getDocs(EnumProperty[] property) {
        return Arrays.stream(property)
                .collect(Collectors.toMap(EnumProperty::name, EnumProperty::getDescription));
    }
}