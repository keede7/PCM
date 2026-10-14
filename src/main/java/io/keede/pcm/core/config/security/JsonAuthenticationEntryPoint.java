package io.keede.pcm.core.config.security;

import io.keede.pcm.core.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증 실패(토큰 없음, 만료, 위조) 시 401과 함께 공통 에러 형식의 JSON을 반환한다.
 */
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String BODY = """
            {"code":"%s","message":"%s"}""".formatted(ErrorCode.UNAUTHORIZED.name(), ErrorCode.UNAUTHORIZED.getMessage());

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(BODY);
    }
}
