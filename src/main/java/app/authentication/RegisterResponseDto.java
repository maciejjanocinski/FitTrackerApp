package app.authentication;

import lombok.Builder;

@Builder
record RegisterResponseDto(
        String jwt,
        String name
) {
}
