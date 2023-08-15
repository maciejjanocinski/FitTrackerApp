package app.user;

import app.util.passwordValidation.ValidPassword;

public record UpdatePasswordDto(@ValidPassword(message = "Old password is not valid")
                                String oldPassword,
                                @ValidPassword(message = "Old password is not valid")
                                String confirmOldPassword,
                                @ValidPassword(message = "New password is not valid")
                                String newPassword) {
}

