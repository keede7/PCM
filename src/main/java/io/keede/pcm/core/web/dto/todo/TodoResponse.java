package io.keede.pcm.core.web.dto.todo;

import io.keede.pcm.core.domains.todo.entity.Todo;

import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        String title,
        String content,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TodoResponse from(
            final Todo todo
    ) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContent(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
