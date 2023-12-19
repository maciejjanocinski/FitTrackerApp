package app.bodyMetrics;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AddBodyMetricsDto(
        @NotNull(message = "You have to pass your gender.")
        Gender gender,
        @NotNull(message = "You have to pass your birth date.")
        LocalDate birthDate,
        @NotNull(message = "You have to pass your height.")
        Double height,
        @NotNull(message = "You have to pass your weight.")
        Double weight,
        @NotNull(message = "You have to pass your neck size (cm).")
        Double neck,
        @NotNull(message = "You have to pass your waist size (cm).")
        Double waist,
        @NotNull(message = "You have to pass your hip size (cm).")
        Double hip) {
}
