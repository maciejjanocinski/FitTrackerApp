package app.authentication;

import lombok.Builder;

@Builder
public record RegisterResponseDto(
        String jwt,
        String name
) {
}
