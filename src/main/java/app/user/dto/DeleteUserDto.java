package app.user.dto;

import app.util.validation.passwordvalidation.ValidPassword;
import lombok.Builder;

@Builder
public record DeleteUserDto(@ValidPassword String password) {
}
