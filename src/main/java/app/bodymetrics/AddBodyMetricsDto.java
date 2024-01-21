package app.bodymetrics;

import app.common.Gender;
import app.util.validation.genderValidation.ValidGender;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
record AddBodyMetricsDto(
        @NotNull(message = "You have to pass your gender.")
        @ValidGender
        Gender gender,
        @NotNull(message = "You have to pass your birth date.")
        LocalDate birthDate,
        @NotNull(message = "You have to pass your height.")
        BigDecimal height,
        @NotNull(message = "You have to pass your weight.")
        BigDecimal weight,
        @NotNull(message = "You have to pass your neck size (cm).")
        BigDecimal neck,
        @NotNull(message = "You have to pass your waist size (cm).")
        BigDecimal waist,
        @NotNull(message = "You have to pass your hip size (cm).")
        BigDecimal hip) {
}
