package io.keede.pcm.core.domains.common.converter;

import io.keede.pcm.core.config.DateTimePatterns;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

/**
 * 모든 LocalDate 필드를 `yyyy-MM-dd` 문자열로 저장한다.
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return attribute == null ? null : attribute.format(DateTimePatterns.DATE_FORMATTER);
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LocalDate.parse(dbData, DateTimePatterns.DATE_FORMATTER);
    }
}
