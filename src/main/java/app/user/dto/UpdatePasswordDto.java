package app.user.dto;

import app.util.passwordValidation.ValidPassword;

public record UpdatePasswordDto(@ValidPassword String oldPassword,
                                @ValidPassword String confirmOldPassword,
                                @ValidPassword String newPassword) {
}

