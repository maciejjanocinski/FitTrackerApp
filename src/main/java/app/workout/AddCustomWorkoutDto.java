package app.workout;

import java.math.BigDecimal;

 record AddCustomWorkoutDto(
        String workoutType,
        String description,
        BigDecimal kcalBurned,
        int intensityLevel,
        Double durationInMinutes
) {
}
