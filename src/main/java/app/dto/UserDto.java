package app.dto;


import app.utils.passwordValidation.PasswordValidator;
import app.utils.passwordValidation.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.ClockProvider;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Size(min = 6, message = "Username must have at least 6 characters.")
    @Size(max = 20, message = "Username cannot have more than 20 characters.")
    @Column(unique = true)
    private String username;

    @ValidPassword
    private String password;

    @NotEmpty(message = "You have to pass your name.")
    private String name;

    @NotEmpty(message = "You have to pass your surname.")
    private String surname;

    @NotEmpty(message = "You have to pass your gender.")
    @Size(max = 1, min = 1, message = "One character is enough.")
    private String gender;

    @NotEmpty(message = "You have to pass your email.")
    @Column(unique = true)
    @Email
    private String email;

    @Size(min = 9, max = 9, message = "Phone number must contain 9 digits.")
    private String phone;

    public String setPasswordWithValidation(String password) {
        PasswordValidator passwordValidator = new PasswordValidator();
        if (passwordValidator.isValidSetterCheck(password) == null) {
            this.password = password;
            return null;
        }
        return passwordValidator.isValidSetterCheck(password);
    }
}

