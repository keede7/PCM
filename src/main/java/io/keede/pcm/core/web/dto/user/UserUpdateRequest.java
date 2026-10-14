package io.keede.pcm.core.web.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param password 변경할 비밀번호. 생략 시 기존 비밀번호를 유지한다.
 */
public record UserUpdateRequest(
        @NotBlank @Size(max = 50) String name,
        @Size(min = 8, max = 64) String password
) {
}
