package app.util.validation.pastdatevalidation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PastDateValidator.class)
@Documented
public @interface PastDate {
    String message() default "Date must be in the past";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}