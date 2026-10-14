package io.keede.pcm.core.web.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TodoUpdateRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 1000) String content,
        @NotNull Boolean completed
) {
}
