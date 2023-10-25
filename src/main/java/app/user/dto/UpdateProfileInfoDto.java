package app.user.dto;

import app.util.GenderValidation.ValidGender;
import app.util.PhoneValidation.ValidPhone;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
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

        @ValidGender
        String gender,

        @NotEmpty(message = "You have to pass your email.")
        @Column(unique = true)
        @Email
        String email,

        @ValidPhone
        String phone) {
}
