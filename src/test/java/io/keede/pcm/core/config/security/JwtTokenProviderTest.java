package io.keede.pcm.core.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "a".repeat(32);

    @Test
    @DisplayName("Access Token에 사용자 ID(subject)와 토큰 키를 담아 발급한다")
    void issueAccessToken() {
        JwtProperties properties = new JwtProperties(SECRET, Duration.ofHours(1));
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(new JwtConfig(properties).jwtEncoder(), properties);
        JwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                .build();

        Jwt jwt = jwtDecoder.decode(jwtTokenProvider.issueAccessToken(1L, "token-key"));

        assertThat(jwt.getSubject()).isEqualTo("1");
        assertThat(jwt.getClaimAsString(JwtTokenProvider.TOKEN_KEY_CLAIM)).isEqualTo("token-key");
    }
}
