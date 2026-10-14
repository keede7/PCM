package io.keede.pcm.core.domains.todo.service;

import io.keede.pcm.core.domains.todo.entity.Todo;
import io.keede.pcm.core.domains.todo.entity.TodoRepository;
import io.keede.pcm.core.domains.todo.fixture.TodoFixture;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.entity.UserRepository;
import io.keede.pcm.core.domains.user.fixture.UserFixture;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.todo.TodoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoCommandServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoCommandService todoCommandService;

    @Test
    @DisplayName("TODO를 생성하면 작성자와 연관되어 미완료 상태로 저장된다")
    void create() {
        User user = UserFixture.builder().id(1L).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(todoRepository.save(any(Todo.class))).willAnswer(invocation -> invocation.getArgument(0));

        TodoResponse response = todoCommandService.create(1L, "장보기", "우유, 계란");

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(response.title()).isEqualTo("장보기");
        assertThat(response.content()).isEqualTo("우유, 계란");
        assertThat(response.completed()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 TODO를 생성하면 예외가 발생한다")
    void createWithUnknownUser() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoCommandService.create(99L, "장보기", null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    @DisplayName("본인의 TODO를 수정한다")
    void update() {
        Todo todo = TodoFixture.builder().id(1L).build();
        given(todoRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(todo));

        TodoResponse response = todoCommandService.update(1L, 1L, "운동", "헬스장 가기", true);

        assertThat(response.title()).isEqualTo("운동");
        assertThat(response.content()).isEqualTo("헬스장 가기");
        assertThat(response.completed()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않거나 타인의 TODO를 수정하면 예외가 발생한다")
    void updateNotFound() {
        given(todoRepository.findByIdAndUserId(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoCommandService.update(1L, 99L, "운동", null, false))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TODO_NOT_FOUND);
    }

    @Test
    @DisplayName("본인의 TODO를 삭제한다")
    void delete() {
        Todo todo = TodoFixture.builder().id(1L).build();
        given(todoRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(todo));

        todoCommandService.delete(1L, 1L);

        verify(todoRepository).delete(todo);
    }

    @Test
    @DisplayName("존재하지 않거나 타인의 TODO를 삭제하면 예외가 발생한다")
    void deleteNotFound() {
        given(todoRepository.findByIdAndUserId(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> todoCommandService.delete(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TODO_NOT_FOUND);
        verify(todoRepository, never()).delete(any(Todo.class));
    }
}
