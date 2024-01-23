package app.authentication;

import app.util.validation.passwordvalidation.ValidPassword;
import app.util.validation.phonevalidation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
record RegisterDto(
        @Size(min = 6, message = "Username must have at least 6 characters.")
        @Size(max = 20, message = "Username cannot have more than 20 characters.")
        String username,

        @ValidPassword
        String password,

        @ValidPassword
        String confirmPassword,

        @NotEmpty(message = "You have to pass your name.")
        String name,

        @NotEmpty(message = "You have to pass your surname.")
        String surname,

        @NotEmpty(message = "You have to pass your email.")
        @Email
        String email,

        @ValidPhone
        String phone) {

}

