package app.bodymetrics;

import app.common.Gender;
import app.util.validation.gendervalidation.ValidGender;
import app.util.validation.pastdatevalidation.PastDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BodyMetricsDto(
        @NotNull(message = "You have to pass your gender.")
        @ValidGender
        Gender gender,
        @NotNull(message = "You have to pass your birth date.")
        @PastDate(message = "Birth date must be in the past.")
        LocalDate birthDate,
        @NotNull(message = "You have to pass your height.")
        @Positive(message = "Height must be positive.")
        @Min(value = 130, message = "Height must be greater than 130.")
        @Max(value = 230, message = "Height must be less than 230.")
        BigDecimal height,
        @NotNull(message = "You have to pass your weight.")
        @Positive(message = "Weight must be positive.")
        @Min(value = 40, message = "Weight must be greater than 40.")
        @Max(value = 160, message = "Weight must be less than 160.")
        BigDecimal weight,
        @NotNull(message = "You have to pass your neck size (cm).")
        @Positive(message = "Neck size must be positive.")
        BigDecimal neck,
        @NotNull(message = "You have to pass your waist size (cm).")
        @Positive(message = "Waist size must be positive.")
        BigDecimal waist,
        @NotNull(message = "You have to pass your hip size (cm).")
        @Positive(message = "Hip size must be positive.")
        BigDecimal hip) {
}
