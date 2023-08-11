package com.bamdoliro.maru.presentation.user.dto.response;

import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import lombok.Getter;

@Getter
public class UserResponse {

    private final String email;
    private final String name;
    private final Authority authority;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.authority = user.getAuthority();
    }
}
