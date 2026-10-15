package io.keede.pcm.core.config.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private final SecretKey secretKey;

    private static final int MIN_SECRET_BYTES = 32;

    public JwtConfig(
            final JwtProperties jwtProperties
    ) {
        this.secretKey = new SecretKeySpec(toSecretBytes(jwtProperties.secret()), "HmacSHA256");
    }

    /**
     * HS256은 256bit 이상의 키가 필요하므로, 부족하면 로그인 시점이 아닌 기동 시점에 실패시킨다.
     */
    private static byte[] toSecretBytes(
            final String secret
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("pcm.jwt.secret(JWT_SECRET)은 " + MIN_SECRET_BYTES + "바이트 이상이어야 합니다.");
        }
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    /**
     * 기본 검증(서명, 만료 시간)에 더해 토큰의 사용자가 유효한지(탈퇴 여부) 검증한다.
     */
    @Bean
    public JwtDecoder jwtDecoder(
            final TokenOwnerValidator tokenOwnerValidator
    ) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                tokenOwnerValidator
        ));
        return jwtDecoder;
    }
}
