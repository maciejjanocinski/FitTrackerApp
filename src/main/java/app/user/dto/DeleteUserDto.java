package app.user.dto;

import app.util.validation.passwordValidation.ValidPassword;
import lombok.Builder;

@Builder
public record DeleteUserDto(@ValidPassword String password) {
}
