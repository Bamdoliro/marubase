package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.user.domain.Verification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.shared.constants.FixedNumber;
import com.bamdoliro.maru.shared.util.RandomCodeUtil;

import java.util.List;
import java.util.stream.IntStream;

public class UserFixture {

    public static User createUser() {
        return new User("01085852525", "김밤돌", "비밀번호", Authority.USER);
    }

    public static User createAdminUser() {
        return new User("01085852525", "김드민", "비밀번호", Authority.ADMIN);
    }

    public static Verification createVerification() {
        return new Verification("01085852525");
    }

    public static List<User> generateUserList() {
        return IntStream.range(0, FixedNumber.TOTAL * 2)
                .mapToObj(i -> new User(RandomCodeUtil.generate(11), "김밤돌" + i, "비밀번호", Authority.USER))
                .toList();
    }
}
