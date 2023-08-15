package app.util.passwordValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Password cannot be null.")
                    .addConstraintViolation();
            return false;
        }

        if (password.length() < 8 || password.length() > 20) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Password must have 8-20 characters.")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[0-9].*")) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Password must have at least one digit.")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Password must have at least one lowercase letter.")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Password must have at least one uppercase letter.")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[!@#&()].*")) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Password must have at least one special character like ! @ # & ( ).")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    public boolean isValidSetterCheck(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("Password must have 8-20 characters.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must have at least one digit.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must have at least one lowercase letter.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must have at least one uppercase letter.");
        }
        if (!password.matches(".*[!@#&()].*")) {
            throw new IllegalArgumentException("Password must have at least one special character like ! @ # & ( ).");
        }
        return true;
    }

}
