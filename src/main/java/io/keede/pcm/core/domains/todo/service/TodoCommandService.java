package io.keede.pcm.core.domains.todo.service;

import io.keede.pcm.core.domains.todo.entity.Todo;
import io.keede.pcm.core.domains.todo.entity.TodoRepository;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.entity.UserRepository;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.todo.TodoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoCommandService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoCommandService(
            final TodoRepository todoRepository,
            final UserRepository userRepository
    ) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public TodoResponse create(
            final Long userId,
            final String title,
            final String content
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return TodoResponse.from(todoRepository.save(Todo.create(user, title, content)));
    }

    public TodoResponse update(
            final Long userId,
            final Long todoId,
            final String title,
            final String content,
            final boolean completed
    ) {
        Todo todo = findMyTodo(userId, todoId);
        todo.update(title, content, completed);
        return TodoResponse.from(todo);
    }

    public void delete(
            final Long userId,
            final Long todoId
    ) {
        todoRepository.delete(findMyTodo(userId, todoId));
    }

    /**
     * 타인의 TODO는 존재 여부를 노출하지 않도록 404로 처리한다.
     */
    private Todo findMyTodo(
            final Long userId,
            final Long todoId
    ) {
        return todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));
    }
}
