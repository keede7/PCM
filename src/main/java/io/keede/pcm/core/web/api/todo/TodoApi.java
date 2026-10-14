package io.keede.pcm.core.web.api.todo;

import io.keede.pcm.core.domains.todo.service.TodoCommandService;
import io.keede.pcm.core.domains.todo.service.TodoQueryService;
import io.keede.pcm.core.web.auth.LoginUserId;
import io.keede.pcm.core.web.dto.todo.TodoCreateRequest;
import io.keede.pcm.core.web.dto.todo.TodoResponse;
import io.keede.pcm.core.web.dto.todo.TodoUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoApi {

    private final TodoCommandService todoCommandService;
    private final TodoQueryService todoQueryService;

    public TodoApi(
            final TodoCommandService todoCommandService,
            final TodoQueryService todoQueryService
    ) {
        this.todoCommandService = todoCommandService;
        this.todoQueryService = todoQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(
            @LoginUserId final Long userId,
            @Valid @RequestBody final TodoCreateRequest request
    ) {
        return todoCommandService.create(userId, request.title(), request.content());
    }

    @GetMapping
    public List<TodoResponse> getMyTodos(@LoginUserId Long userId) {
        return todoQueryService.getMyTodos(userId);
    }

    @GetMapping("/search")
    public List<TodoResponse> search(
            @LoginUserId final Long userId,
            @RequestParam @NotBlank final String keyword
    ) {
        return todoQueryService.search(userId, keyword);
    }

    @GetMapping("/{id}")
    public TodoResponse getMyTodo(
            @LoginUserId final Long userId,
            @PathVariable final Long id
    ) {
        return todoQueryService.getMyTodo(userId, id);
    }

    @PutMapping("/{id}")
    public TodoResponse update(
            @LoginUserId final Long userId,
            @PathVariable final Long id,
            @Valid @RequestBody final TodoUpdateRequest request
    ) {
        return todoCommandService.update(userId, id, request.title(), request.content(), request.completed());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @LoginUserId final Long userId,
            @PathVariable final Long id
    ) {
        todoCommandService.delete(userId, id);
    }
}
