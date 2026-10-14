package io.keede.pcm.core.domains.todo.fixture;

import io.keede.pcm.core.domains.todo.entity.Todo;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.fixture.UserFixture;
import org.springframework.test.util.ReflectionTestUtils;

public final class TodoFixture {

    private TodoFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id = 1L;
        private User user = UserFixture.builder().build();
        private String title = "장보기";
        private String content = "우유, 계란";
        private boolean completed = false;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder completed(boolean completed) {
            this.completed = completed;
            return this;
        }

        public Todo build() {
            Todo todo = Todo.create(user, title, content);
            todo.update(title, content, completed);
            ReflectionTestUtils.setField(todo, "id", id);
            return todo;
        }
    }
}
