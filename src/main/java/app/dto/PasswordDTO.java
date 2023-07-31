package app.dto;

import app.utils.passwordValidation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    @ValidPassword(message = "Old password is not valid")
    private String oldPassword;

    @ValidPassword(message = "Old password is not valid")
    private String confirmOldPassword;

    @ValidPassword(message = "New password is not valid")
    private String newPassword;
}
