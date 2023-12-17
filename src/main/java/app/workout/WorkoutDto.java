package app.workout;

import java.util.Date;

public record WorkoutDto(
         Long id,
         String activityId,
         String workoutType,
         String description,
         Double kcalBurned,
         Double durationInMinutes,
         int intensityLevel,
         Date date
 ) {}
