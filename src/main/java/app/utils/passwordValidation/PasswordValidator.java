package app.utils.passwordValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        if (password.length() < 8 || password.length() > 20) {
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            return false; // Musi zawierać co najmniej jedną cyfrę
        }

        if (!password.matches(".*[a-z].*")) {
            return false; // Musi zawierać co najmniej jedną małą literę
        }

        if (!password.matches(".*[A-Z].*")) {
            return false; // Musi zawierać co najmniej jedną wielką literę
        }

        if (!password.matches(".*[!@#&()].*")) {
            return false; // Musi zawierać co najmniej jeden znak specjalny
        }

        return true;
    }
}
