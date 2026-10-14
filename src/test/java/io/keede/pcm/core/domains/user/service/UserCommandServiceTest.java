package io.keede.pcm.core.domains.user.service;

import io.keede.pcm.core.domains.todo.entity.TodoRepository;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.entity.UserRepository;
import io.keede.pcm.core.domains.user.fixture.UserFixture;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCommandService userCommandService;

    @Test
    @DisplayName("회원가입 시 비밀번호를 암호화하여 저장하고 응답 DTO를 반환한다")
    void signup() {
        given(userRepository.existsByEmail("keede@pcm.io")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userCommandService.signup("keede@pcm.io", "password123", "keede");

        assertThat(response.email()).isEqualTo("keede@pcm.io");
        assertThat(response.name()).isEqualTo("keede");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 가입된 이메일이면 예외가 발생한다")
    void signupWithDuplicateEmail() {
        given(userRepository.existsByEmail("keede@pcm.io")).willReturn(true);

        assertThatThrownBy(() -> userCommandService.signup("keede@pcm.io", "password123", "keede"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원정보 수정 시 이름과 비밀번호를 변경한다")
    void update() {
        User user = UserFixture.builder().id(1L).name("keede").password("old-password").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.encode("newPassword123")).willReturn("new-encoded-password");

        UserResponse response = userCommandService.update(1L, "new-name", "newPassword123");

        assertThat(response.name()).isEqualTo("new-name");
        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    @DisplayName("회원정보 수정 시 비밀번호를 입력하지 않으면 기존 비밀번호를 유지한다")
    void updateWithoutPassword() {
        User user = UserFixture.builder().id(1L).password("old-password").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        userCommandService.update(1L, "new-name", null);

        assertThat(user.getPassword()).isEqualTo("old-password");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 정보를 수정하면 예외가 발생한다")
    void updateNotFound() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userCommandService.update(99L, "new-name", null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 탈퇴 시 사용자의 TODO를 먼저 삭제한 뒤 사용자를 삭제한다")
    void withdraw() {
        User user = UserFixture.builder().id(1L).build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        userCommandService.withdraw(1L);

        var order = inOrder(todoRepository, userRepository);
        order.verify(todoRepository).deleteAllByUserId(1L);
        order.verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 탈퇴하면 예외가 발생한다")
    void withdrawNotFound() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userCommandService.withdraw(99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
        verify(todoRepository, never()).deleteAllByUserId(any());
    }
}
