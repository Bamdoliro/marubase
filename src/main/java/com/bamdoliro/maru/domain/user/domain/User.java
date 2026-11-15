package com.bamdoliro.maru.domain.user.domain;

import com.bamdoliro.maru.domain.user.domain.type.Authority;
import com.bamdoliro.maru.domain.user.domain.value.Password;
import com.bamdoliro.maru.infrastructure.persistence.converter.StringEncryptedConverter;
import com.bamdoliro.maru.shared.util.PasswordUtil;
import com.bamdoliro.maru.shared.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tbl_user")
@Entity
public class User extends BaseTimeEntity {

    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID uuid;

    @Convert(converter = StringEncryptedConverter.class)
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Convert(converter = StringEncryptedConverter.class)
    @Column(nullable = false)
    private String name;

    @Embedded
    private Password password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Authority authority;

    @Column(nullable = false)
    private boolean isTestData;

    public User(String phoneNumber, String name, String password, Authority authority) {
        this(phoneNumber, name, password, authority, false);
    }

    @Builder
    public User(String phoneNumber, String name, String password, Authority authority, boolean isTestData) {
        this(phoneNumber, name, new Password(PasswordUtil.encode(password)), authority, isTestData);
    }

    private User(String phoneNumber, String name, Password password, Authority authority, boolean isTestData) {
        this.uuid = UUID.randomUUID();
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.password = password;
        this.authority = authority;
        this.isTestData = isTestData;
    }

    // 테스트 데이터를 대량 생성할 때 BCrypt 인코딩 비용을 줄이기 위해 미리 인코딩한 비밀번호를 받는다.
    public static User createTestData(String phoneNumber, String name, Password encodedPassword) {
        return new User(phoneNumber, name, encodedPassword, Authority.USER, true);
    }

    public boolean isAdmin() {
        return this.authority == Authority.ADMIN;
    }

    public void updatePassword(String password) {
        this.password = new Password(PasswordUtil.encode(password));
    }
}
