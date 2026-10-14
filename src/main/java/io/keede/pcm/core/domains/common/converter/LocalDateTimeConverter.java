package io.keede.pcm.core.domains.common.converter;

import io.keede.pcm.core.config.DateTimePatterns;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;

/**
 * 모든 LocalDateTime 필드를 `yyyy-MM-ddTHH:mm:ss` 문자열로 저장한다.
 */
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute == null ? null : attribute.format(DateTimePatterns.STORED_DATE_TIME_FORMATTER);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LocalDateTime.parse(dbData, DateTimePatterns.STORED_DATE_TIME_FORMATTER);
    }
}
