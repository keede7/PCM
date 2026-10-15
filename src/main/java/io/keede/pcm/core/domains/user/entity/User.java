package io.keede.pcm.core.domains.user.entity;

import io.keede.pcm.core.domains.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    /**
     * 토큰 발급 시 함께 담기는 사용자별 고유 키.
     * 탈퇴로 사용자가 삭제되면 기존 토큰의 키와 일치하는 사용자가 없어 토큰이 만료된다.
     * (SQLite는 삭제된 ID를 재사용할 수 있으므로 ID만으로는 사용자를 구분할 수 없다.)
     */
    @Column(nullable = false, unique = true)
    private String tokenKey;

    protected User() {
    }

    private User(
            final String email,
            final String password,
            final String name
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.tokenKey = UUID.randomUUID().toString();
    }

    public static User create(
            final String email,
            final String encodedPassword,
            final String name
    ) {
        return new User(email, encodedPassword, name);
    }

    public void changeName(
            final String name
    ) {
        this.name = name;
    }

    public void changePassword(
            final String encodedPassword
    ) {
        this.password = encodedPassword;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getTokenKey() {
        return tokenKey;
    }
}
