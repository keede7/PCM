package io.keede.pcm.core.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @param secret     HS256 서명 키 (32바이트 이상)
 * @param expiration Access Token 만료 시간
 */
@ConfigurationProperties(prefix = "pcm.jwt")
public record JwtProperties(
        String secret,
        Duration expiration
) {
}
