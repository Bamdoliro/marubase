package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.domain.user.domain.User;

public class UserFixture {

    public static User createUser() {
        return new User("maru@bamdoliro.com", "비밀번호");
    }

    public static EmailVerification createVerification() {
        return new EmailVerification("maru@bamdoliro.com");
    }
}
