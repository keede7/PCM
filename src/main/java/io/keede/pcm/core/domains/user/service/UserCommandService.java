package io.keede.pcm.core.domains.user.service;

import io.keede.pcm.core.domains.todo.entity.TodoRepository;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.entity.UserRepository;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserCommandService {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCommandService(
            final UserRepository userRepository,
            final TodoRepository todoRepository,
            final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse signup(
            final String email,
            final String rawPassword,
            final String name
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        User user = User.create(email, passwordEncoder.encode(rawPassword), name);
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * 이름을 변경한다. 비밀번호는 입력된 경우에만 변경한다.
     */
    public UserResponse update(
            final Long userId,
            final String name,
            final String rawPassword
    ) {
        User user = findUser(userId);
        user.changeName(name);
        if (rawPassword != null) {
            user.changePassword(passwordEncoder.encode(rawPassword));
        }
        return UserResponse.from(user);
    }

    public void withdraw(Long userId) {
        User user = findUser(userId);
        todoRepository.deleteAllByUserId(userId);
        userRepository.delete(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
