package app.dto;

import app.models.UserEntity;

public record LoginResponseDto(UserEntity userEntity, String jwt) {
}
