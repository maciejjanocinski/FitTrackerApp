package app.authentication;

import app.util.validation.passwordValidation.ValidPassword;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterDto(
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

        @NotEmpty(message = "You have to pass your gender.")
        String gender,

        @NotEmpty(message = "You have to pass your email.")
        @Email
        String email,

        @Size(min = 9, max = 9, message = "Phone number must contain 9 digits.")
        String phone) {

}

