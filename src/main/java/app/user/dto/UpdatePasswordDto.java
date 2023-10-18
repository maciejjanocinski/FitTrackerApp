package app.user.dto;

import app.util.passwordValidation.ValidPassword;
import lombok.Builder;

@Builder
public record UpdatePasswordDto(@ValidPassword String oldPassword,
                                @ValidPassword String newPassword,
                                @ValidPassword String confirmNewPassword) {
}

