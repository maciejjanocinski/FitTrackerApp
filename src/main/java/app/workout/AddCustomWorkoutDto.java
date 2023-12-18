package app.workout;

public record AddCustomWorkoutDto(
        String workoutType,
        String description,
        Double kcalBurned,
        int intensityLevel,
        Double durationInMinutes
) {
}
