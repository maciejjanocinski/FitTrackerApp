package app.dto;

import app.models.User;

public record LoginResponseDto(User userEntity, String jwt) {
}
