package app.util.validation.genderValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
@Documented
public @interface ValidGender {
    String message() default "Invalid gender syntax";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
