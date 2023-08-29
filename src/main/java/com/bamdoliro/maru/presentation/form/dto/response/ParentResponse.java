package com.bamdoliro.maru.presentation.form.dto.response;

import com.bamdoliro.maru.domain.form.domain.value.Parent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParentResponse {

    private String name;
    private String phoneNumber;
    private String relation;
    private String zoneCode;
    private String address;
    private String detailAddress;

    public ParentResponse(Parent parent) {
        this.name = parent.getName();
        this.phoneNumber = parent.getPhoneNumber().toString();
        this.relation = parent.getRelation();
        this.zoneCode = parent.getAddress().getZoneCode();
        this.address = parent.getAddress().getAddress();
        this.detailAddress = parent.getAddress().getDetailAddress();
    }
}
