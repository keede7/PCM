package io.keede.pcm.core.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * API 요청/응답의 날짜/시간 형식을 전역으로 지정한다.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer dateTimeFormatCustomizer() {
        return builder -> builder
                .withConfigOverride(LocalDateTime.class,
                        override -> override.setFormat(JsonFormat.Value.forPattern(DateTimePatterns.DATE_TIME)))
                .withConfigOverride(LocalDate.class,
                        override -> override.setFormat(JsonFormat.Value.forPattern(DateTimePatterns.DATE)));
    }
}
