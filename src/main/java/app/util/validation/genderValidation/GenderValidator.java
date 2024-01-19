package app.util.validation.genderValidation;

import app.common.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.UnexpectedTypeException;

import java.util.Objects;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validateGender(value);
    }

    public boolean validateGender(String gender) {
        if(gender.equals("MALE") || gender.equals("FEMALE")) return true;
        throw new UnexpectedTypeException("Gender doesn't match any of the values. Should be \"MALE\" or \"FEMALE\".");
    }
}
