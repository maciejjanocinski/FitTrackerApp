package app.workout;

import java.math.BigDecimal;

public record AddCustomWorkoutDto(
        String workoutType,
        String description,
        BigDecimal kcalBurned,
        int intensityLevel,
        Double durationInMinutes
) {
}
