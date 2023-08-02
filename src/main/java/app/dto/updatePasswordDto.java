package app.dto;

import app.utils.passwordValidation.ValidPassword;

public record updatePasswordDto(@ValidPassword(message = "Old password is not valid")
                                String oldPassword,
                                @ValidPassword(message = "Old password is not valid")
                                String confirmOldPassword,
                                @ValidPassword(message = "New password is not valid")
                                String newPassword) {
}