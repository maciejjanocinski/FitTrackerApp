package app.workout;

import java.time.Duration;
import java.util.Date;

public record WorkoutDto(
         Long id,
         String workoutType,
         String descritpion,
         Double kcalBurned,
         Duration duration,
         int intensityLevel,
         Date date
 ) {}
