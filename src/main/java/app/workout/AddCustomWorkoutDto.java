package app.workout;

import java.time.Duration;

public record AddCustomWorkoutDto(
        String workoutType,
        String description,
        Double kcalBurned,
        int intensityLevel,
        Duration duration
) {
}
