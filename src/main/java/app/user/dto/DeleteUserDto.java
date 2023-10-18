package app.user.dto;

import app.util.passwordValidation.ValidPassword;
import lombok.Builder;

@Builder
public record DeleteUserDto(@ValidPassword String password,
                     @ValidPassword String confirmPassword) {
}
