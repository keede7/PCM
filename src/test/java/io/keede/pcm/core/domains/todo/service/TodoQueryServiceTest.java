package io.keede.pcm.core.domains.todo.service;

import io.keede.pcm.core.domains.todo.entity.Todo;
import io.keede.pcm.core.domains.todo.entity.TodoRepository;
import io.keede.pcm.core.domains.todo.fixture.TodoFixture;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.todo.TodoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoQueryServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoQueryService todoQueryService;

    @Test
    @DisplayName("내 TODO 목록을 응답 DTO로 조회한다")
    void getMyTodos() {
        List<Todo> todos = List.of(
                TodoFixture.builder().id(1L).title("장보기").build(),
                TodoFixture.builder().id(2L).title("운동").build()
        );
        given(todoRepository.findAllByUserIdOrderByIdAsc(1L)).willReturn(todos);

        assertThat(todoQueryService.getMyTodos(1L))
                .extracting(TodoResponse::title)
                .containsExactly("장보기", "운동");
    }

    @Test
    @DisplayName("내 TODO를 단건 조회한다")
    void getMyTodo() {
        Todo todo = TodoFixture.builder().id(1L).title("장보기").build();
        given(todoRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(todo));

        TodoResponse response = todoQueryService.getMyTodo(1L, 1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("장보기");
    }

    @Test
    @DisplayName("존재하지 않거나 타인의 TODO를 조회하면 예외가 발생한다")
    void getMyTodoNotFound() {
        given(todoRepository.findByIdAndUserId(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoQueryService.getMyTodo(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TODO_NOT_FOUND);
    }

    @Test
    @DisplayName("제목에 키워드가 포함된 내 TODO를 검색한다")
    void search() {
        List<Todo> todos = List.of(TodoFixture.builder().id(1L).title("장보기").build());
        given(todoRepository.findAllByUserIdAndTitleContainingOrderByIdAsc(1L, "장보")).willReturn(todos);

        assertThat(todoQueryService.search(1L, "장보"))
                .extracting(TodoResponse::title)
                .containsExactly("장보기");
    }
}
