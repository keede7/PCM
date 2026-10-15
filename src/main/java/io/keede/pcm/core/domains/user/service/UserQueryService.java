package io.keede.pcm.core.domains.user.service;

import io.keede.pcm.core.config.security.JwtTokenProvider;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.entity.UserRepository;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.user.LoginResponse;
import io.keede.pcm.core.web.dto.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserQueryService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserResponse getById(
            final Long userId
    ) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일/비밀번호를 검증하고 Access Token을 발급한다.
     * 계정 존재 여부를 노출하지 않도록 두 경우 모두 같은 예외를 던진다.
     */
    public LoginResponse login(
            final String email,
            final String rawPassword
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        verifyPassword(rawPassword, user);
        return new LoginResponse(jwtTokenProvider.issueAccessToken(user.getId()));
    }

    private void verifyPassword(
            final String rawPassword,
            final User user
    ) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
