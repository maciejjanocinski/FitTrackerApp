package app.user.dto;


import app.bodymetrics.BodyMetricsDto;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDto(
        @Size(min = 6, message = "Username must have at least 6 characters.")
        @Size(max = 20, message = "Username cannot have more than 20 characters.")
        @Column(unique = true)
        String username,

        @NotEmpty(message = "You have to pass your name.")
        String name,

        @NotEmpty(message = "You have to pass your surname.")
        String surname,

        @NotEmpty(message = "You have to pass your email.")
        @Column(unique = true)
        @Email
        String email,

        @Size(min = 9, max = 9, message = "Phone number must contain 9 digits.")
        String phone,

        BodyMetricsDto bodyMetrics


        ) {

}

