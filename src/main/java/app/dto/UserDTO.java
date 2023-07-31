package app.dto;

import app.utils.passwordValidation.ValidPassword;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Size(min = 6, message = "Username must have at least 6 characters.")
    @Size(max = 20, message = "Username cannot have more than 20 characters.")
    @Column(unique = true)
    private String username;

    @ValidPassword(message = "Password must have 8-20 characters," +
            " at least one lowercase letter," +
            " at least one uppercase letter," +
            " at least one digit," +
            " at least one special character like ! @ # & ( )")
    private String password;

    @NotEmpty(message = "You have to pass your name.")
    private String name;

    @NotEmpty(message = "You have to pass your surname.")
    private String surname;

    @NotEmpty(message = "You have to pass your email.")
    @Column(unique = true)
    @Email
    private String email;

    @Size( min = 9, max = 9, message = "Phone number must contain 9 digits.")
    private String phone;


}
