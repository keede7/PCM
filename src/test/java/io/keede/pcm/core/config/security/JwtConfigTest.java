package io.keede.pcm.core.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtConfigTest {

    @Test
    @DisplayName("JWT 서명 키가 32바이트 미만이면 애플리케이션 기동 시 실패한다")
    void shortSecret() {
        JwtProperties properties = new JwtProperties("short-secret", Duration.ofHours(1));

        assertThatThrownBy(() -> new JwtConfig(properties))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("JWT 서명 키가 없으면 애플리케이션 기동 시 실패한다")
    void missingSecret() {
        JwtProperties properties = new JwtProperties(null, Duration.ofHours(1));

        assertThatThrownBy(() -> new JwtConfig(properties))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("JWT 서명 키가 32바이트 이상이면 정상 생성된다")
    void validSecret() {
        JwtProperties properties = new JwtProperties("a".repeat(32), Duration.ofHours(1));

        assertThatCode(() -> new JwtConfig(properties)).doesNotThrowAnyException();
    }
}
