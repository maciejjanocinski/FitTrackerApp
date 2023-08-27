package app.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UpdateProfileInfoDto(
        @Size(min = 6, message = "Username must have at least 6 characters.")
        @Size(max = 20, message = "Username cannot have more than 20 characters.")
        @Column(unique = true)
        @NotEmpty(message = "You have to pass your username.")
        String username,

        @NotEmpty(message = "You have to pass your name.")
        String name,

        @NotEmpty(message = "You have to pass your surname.")
        String surname,

        @NotEmpty(message = "You have to pass your gender.")
        @Size(max = 1, min = 1, message = "One character is enough.")
        String gender,

        @NotEmpty(message = "You have to pass your email.")
        @Column(unique = true)
        @Email
        String email,

        @Size(min = 9, max = 9, message = "Phone number must contain 9 digits.")
        @NotEmpty(message = "You have to pass your phone number.")
        String phone) {
}
