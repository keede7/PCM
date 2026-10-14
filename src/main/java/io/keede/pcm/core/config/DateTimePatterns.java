package io.keede.pcm.core.config;

import java.time.format.DateTimeFormatter;

/**
 * 날짜/시간 형식.
 * <ul>
 *     <li>저장: 일시는 DB에서 다루기 쉬운 `yyyy-MM-ddTHH:mm:ss` (ISO-8601)</li>
 *     <li>응답: 일시는 `yyyy-MM-dd HH:mm:ss`</li>
 *     <li>날짜는 저장/응답 모두 `yyyy-MM-dd`</li>
 * </ul>
 */
public final class DateTimePatterns {

    public static final String DATE = "yyyy-MM-dd";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String STORED_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE);
    public static final DateTimeFormatter STORED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(STORED_DATE_TIME);

    private DateTimePatterns() {
    }
}
