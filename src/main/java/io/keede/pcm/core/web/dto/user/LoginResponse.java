package io.keede.pcm.core.web.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("access_token") String accessToken
) {
}
