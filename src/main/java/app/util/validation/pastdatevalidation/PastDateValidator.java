package app.util.validation.pastdatevalidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

class PastDateValidator implements ConstraintValidator<PastDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !value.isAfter(LocalDate.now());
    }
}