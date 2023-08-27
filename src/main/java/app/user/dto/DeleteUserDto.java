package app.user.dto;

import app.util.passwordValidation.ValidPassword;

public record DeleteUserDto(@ValidPassword String password,
                     @ValidPassword String confirmPassword) {
}
