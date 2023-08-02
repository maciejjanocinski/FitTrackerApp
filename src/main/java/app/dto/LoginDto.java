package app.dto;

import app.utils.passwordValidation.ValidPassword;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record LoginDto(@Column(unique = true)
                       @Size(min = 6, message = "Username must have at least 6 characters.")
                       @Size(max = 20, message = "Username cannot have more than 20 characters.")
                       String username,
                       @ValidPassword
                       String password) {

}
