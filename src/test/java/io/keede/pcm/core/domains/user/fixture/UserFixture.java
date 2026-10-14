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

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public User build() {
            User user = User.create(email, password, name);
            ReflectionTestUtils.setField(user, "id", id);
            return user;
        }
    }
}
