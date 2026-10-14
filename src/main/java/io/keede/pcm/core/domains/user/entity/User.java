package io.keede.pcm.core.domains.user.entity;

import io.keede.pcm.core.domains.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
    }

    public static User create(
            final String email,
            final String encodedPassword,
            final String name
    ) {
        return new User(email, encodedPassword, name);
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePassword(String encodedPassword) {
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
}
