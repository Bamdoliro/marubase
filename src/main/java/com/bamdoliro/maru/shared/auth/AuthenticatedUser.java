package com.bamdoliro.maru.shared.auth;

import com.bamdoliro.maru.domain.user.domain.User;

public record AuthenticatedUser(Long id) {

    public static AuthenticatedUser from(User user) {
        return new AuthenticatedUser(user.getId());
    }
}
