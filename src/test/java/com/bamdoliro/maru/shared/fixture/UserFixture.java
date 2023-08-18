package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.user.domain.EmailVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.shared.constants.FixedNumber;

import java.util.List;
import java.util.stream.IntStream;

public class UserFixture {

    public static User createUser() {
        return new User("maru@bamdoliro.com", "김밤돌", "비밀번호", Authority.USER);
    }

    public static User createAdminUser() {
        return new User("maru@bamdoliro.com", "김드민", "비밀번호", Authority.ADMIN);
    }

    public static EmailVerification createVerification() {
        return new EmailVerification("maru@bamdoliro.com");
    }

    public static List<User> generateUserList() {
        return IntStream.range(0, FixedNumber.TOTAL * 2)
                .mapToObj(i -> new User("maru" + i + "@bamdoliro.com", "김밤돌" + i, "비밀번호", Authority.USER))
                .toList();
    }
}
