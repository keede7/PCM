package io.keede.pcm.core.domains.user.fixture;

import io.keede.pcm.core.domains.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public final class UserFixture {

    private UserFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id = 1L;
        private String email = "keede@pcm.io";
        private String password = "encoded-password";
        private String name = "keede";
        private String tokenKey = "token-key";

        public Builder id(
                final Long id
        ) {
            this.id = id;
            return this;
        }

        public Builder email(
                final String email
        ) {
            this.email = email;
            return this;
        }

        public Builder password(
                final String password
        ) {
            this.password = password;
            return this;
        }

        public Builder name(
                final String name
        ) {
            this.name = name;
            return this;
        }

        public Builder tokenKey(
                final String tokenKey
        ) {
            this.tokenKey = tokenKey;
            return this;
        }

        public User build() {
            User user = User.create(email, password, name);
            ReflectionTestUtils.setField(user, "id", id);
            ReflectionTestUtils.setField(user, "tokenKey", tokenKey);
            return user;
        }
    }
}
