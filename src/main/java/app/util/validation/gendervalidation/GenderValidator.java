package app.util.validation.gendervalidation;

import app.common.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.UnexpectedTypeException;

public class GenderValidator implements ConstraintValidator<ValidGender, Gender> {
    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        return validateGender(value);
    }

    public boolean validateGender(Gender gender) {
        if(gender.toString().equals("MALE") || gender.toString().equals("FEMALE")) return true;
        throw new UnexpectedTypeException("Gender doesn't match any of the values. Should be \"MALE\" or \"FEMALE\".");
    }
}
