package app.authentication;

import app.util.passwordValidation.ValidPassword;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;

record LoginDto(@Column(unique = true)
                @Size(min = 6, message = "Username must have at least 6 characters.")
                @Size(max = 20, message = "Username cannot have more than 20 characters.")
                String username,
                @ValidPassword
                String password) {

}
