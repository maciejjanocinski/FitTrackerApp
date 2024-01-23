package app.user.dto;

import app.util.validation.passwordvalidation.ValidPassword;
import lombok.Builder;

@Builder
public record UpdatePasswordDto(@ValidPassword String oldPassword,
                                @ValidPassword String newPassword,
                                @ValidPassword String confirmNewPassword) {
}

