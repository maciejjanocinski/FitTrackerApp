package app.user.dto;

import app.util.validation.passwordValidation.ValidPassword;
import lombok.Builder;

@Builder
public record UpdatePasswordDto(@ValidPassword String oldPassword,
                                @ValidPassword String newPassword,
                                @ValidPassword String confirmNewPassword) {
}

