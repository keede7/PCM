package io.keede.pcm.core.exception;

public record ErrorResponse(
        String code,
        String message
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.name(), errorCode.getMessage());
    }

    public static ErrorResponse of(
            final ErrorCode errorCode,
            final String message
    ) {
        return new ErrorResponse(errorCode.name(), message);
    }
}
