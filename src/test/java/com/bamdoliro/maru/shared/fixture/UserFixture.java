package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.user.domain.User;

public class UserFixture {

    public static User createUser() {
        return new User("maru@bamdoliro.com", "해시값은항상다르다");
    }
}
