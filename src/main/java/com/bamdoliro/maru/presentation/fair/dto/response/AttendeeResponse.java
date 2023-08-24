package com.bamdoliro.maru.presentation.fair.dto.response;

import com.bamdoliro.maru.domain.fair.domain.Attendee;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendeeResponse {

    private String schoolName;
    private String name;
    private String type;
    private String phoneNumber;
    private Integer headcount;
    private String question;

    public AttendeeResponse(Attendee attendee) {
        this.schoolName = attendee.getSchoolName();
        this.name = attendee.getName();
        this.type = attendee.getType();
        this.phoneNumber = attendee.getPhoneNumber();
        this.headcount = attendee.getHeadcount();
        this.question = attendee.getQuestion();
    }
}
