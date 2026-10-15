package io.keede.pcm.core.domains.user.service;

import io.keede.pcm.core.config.security.JwtTokenProvider;
import io.keede.pcm.core.domains.user.entity.User;
import io.keede.pcm.core.domains.user.entity.UserRepository;
import io.keede.pcm.core.domains.user.fixture.UserFixture;
import io.keede.pcm.core.exception.BusinessException;
import io.keede.pcm.core.exception.ErrorCode;
import io.keede.pcm.core.web.dto.user.LoginResponse;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    @DisplayName("ID로 사용자를 조회하여 응답 DTO로 반환한다")
    void getById() {
        User user = UserFixture.builder().id(1L).email("keede@pcm.io").name("keede").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        UserResponse response = userQueryService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("keede@pcm.io");
        assertThat(response.name()).isEqualTo("keede");
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 조회하면 예외가 발생한다")
    void getByIdNotFound() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userQueryService.getById(99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 Access Token을 발급한다")
    void login() {
        User user = UserFixture.builder().id(1L).email("keede@pcm.io").password("encoded-password").tokenKey("token-key").build();
        given(userRepository.findByEmail("keede@pcm.io")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encoded-password")).willReturn(true);
        given(jwtTokenProvider.issueAccessToken(1L, "token-key")).willReturn("access-token");

        LoginResponse response = userQueryService.login("keede@pcm.io", "password123");

        assertThat(response.accessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    void loginWithWrongPassword() {
        User user = UserFixture.builder().email("keede@pcm.io").password("encoded-password").build();
        given(userRepository.findByEmail("keede@pcm.io")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "encoded-password")).willReturn(false);

        assertThatThrownBy(() -> userQueryService.login("keede@pcm.io", "wrong-password"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("가입되지 않은 이메일이면 예외가 발생한다")
    void loginWithUnknownEmail() {
        given(userRepository.findByEmail("unknown@pcm.io")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userQueryService.login("unknown@pcm.io", "password123"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("사용자 ID와 토큰 키가 일치하는 사용자가 있으면 유효한 토큰이다")
    void validTokenOwner() {
        given(userRepository.existsByIdAndTokenKey(1L, "token-key")).willReturn(true);

        assertThat(userQueryService.isValidTokenOwner(1L, "token-key")).isTrue();
    }

    @Test
    @DisplayName("탈퇴 등으로 사용자 ID와 토큰 키가 일치하는 사용자가 없으면 유효하지 않은 토큰이다")
    void invalidTokenOwner() {
        given(userRepository.existsByIdAndTokenKey(1L, "old-token-key")).willReturn(false);

        assertThat(userQueryService.isValidTokenOwner(1L, "old-token-key")).isFalse();
    }
}
