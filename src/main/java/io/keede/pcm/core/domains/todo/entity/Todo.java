package io.keede.pcm.core.domains.todo.entity;

import io.keede.pcm.core.domains.common.entity.BaseTimeEntity;
import io.keede.pcm.core.domains.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "todos", indexes = @Index(name = "idx_todos_user_id", columnList = "user_id"))
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean completed;

    protected Todo() {
    }

    private Todo(
            final User user,
            final String title,
            final String content
    ) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.completed = false;
    }

    public static Todo create(
            final User user,
            final String title,
            final String content
    ) {
        return new Todo(user, title, content);
    }

    public void update(
            final String title,
            final String content,
            final boolean completed
    ) {
        this.title = title;
        this.content = content;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isCompleted() {
        return completed;
    }
}
