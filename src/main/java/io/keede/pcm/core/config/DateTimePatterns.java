package io.keede.pcm.core.config;

import java.time.format.DateTimeFormatter;

/**
 * 저장 및 응답에 공통으로 사용하는 날짜/시간 형식.
 */
public final class DateTimePatterns {

    public static final String DATE = "yyyy-MM-dd";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME);

    private DateTimePatterns() {
    }
}
