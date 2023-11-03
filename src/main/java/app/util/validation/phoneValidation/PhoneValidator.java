package app.util.validation.phoneValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.UnexpectedTypeException;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validPhone(value);
    }

    private boolean validPhone(String number) {
        if (
                number.matches(".*[a-z].*") ||
                number.matches(".*[A-Z].*") ||
                number.matches(".*[!@#&()].*") ||
                number.split("").length != 9
                ) {
            throw new UnexpectedTypeException("Phone number must contain 9 digits.");
        }
        return true;
    }
}
