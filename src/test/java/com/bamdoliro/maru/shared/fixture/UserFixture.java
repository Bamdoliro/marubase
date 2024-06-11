package com.bamdoliro.maru.shared.fixture;

import com.bamdoliro.maru.domain.user.domain.SignUpVerification;
import com.bamdoliro.maru.domain.user.domain.UpdatePasswordVerification;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.shared.util.RandomCodeUtil;

import java.util.List;
import java.util.stream.IntStream;

import static com.bamdoliro.maru.shared.util.RandomUtil.randomPhoneNumber;

public class UserFixture {

    public static User createUser() {
        return new User(randomPhoneNumber(), "김밤돌", "비밀번호", Authority.USER);
    }

    public static User createAdminUser() {
        return new User("01085852525", "김드민", "비밀번호", Authority.ADMIN);
    }

    public static SignUpVerification createSignUpVerification(boolean verified) {
        SignUpVerification signUpVerification = new SignUpVerification("01085852525");
        if (verified) {
            signUpVerification.verify();
        }

        return signUpVerification;
    }

    public static UpdatePasswordVerification createUpdatePasswordVerification(boolean verified) {
        UpdatePasswordVerification updatePasswordVerification = new UpdatePasswordVerification("01085852525");
        if (verified) {
            updatePasswordVerification.verify();
        }

        return updatePasswordVerification;
    }

    public static List<User> generateUserList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new User(RandomCodeUtil.generate(11), "김밤돌" + i, "비밀번호", Authority.USER))
                .toList();
    }
}
