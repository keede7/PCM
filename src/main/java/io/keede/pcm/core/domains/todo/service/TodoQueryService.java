package io.keede.pcm.core.domains.todo.service;

import io.keede.pcm.core.domains.todo.entity.TodoRepository;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.todo.TodoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoRepository todoRepository;

    public TodoQueryService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoResponse> getMyTodos(Long userId) {
        return todoRepository.findAllByUserIdOrderByIdAsc(userId).stream()
                .map(TodoResponse::from)
                .toList();
    }

    public TodoResponse getMyTodo(
            final Long userId,
            final Long todoId
    ) {
        return todoRepository.findByIdAndUserId(todoId, userId)
                .map(TodoResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));
    }

    public List<TodoResponse> search(
            final Long userId,
            final String keyword
    ) {
        return todoRepository.findAllByUserIdAndTitleContainingOrderByIdAsc(userId, keyword.strip()).stream()
                .map(TodoResponse::from)
                .toList();
    }
}
