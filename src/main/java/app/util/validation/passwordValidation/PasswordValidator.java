package app.util.validation.passwordValidation;

import app.exceptions.InvalidPasswordException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return validation(password);
    }

    public boolean isValidSetterCheck(String password) {
        return validation(password);
    }

    private boolean validation(String password) {
        if (password == null) {
            throw new InvalidPasswordException("Password cannot be null.");
        }

        if (password.length() < 8 || password.length() > 20) {
            throw new InvalidPasswordException("Password must have 8-20 characters.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new InvalidPasswordException("Password must have at least one digit.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new InvalidPasswordException("Password must have at least one lowercase letter.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("Password must have at least one uppercase letter.");
        }
        if (!password.matches(".*[!@#&()].*")) {
            throw new InvalidPasswordException("Password must have at least one special character like ! @ # & ( ).");
        }
        return true;
    }

}
