package app.workout;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/workout")
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping("/")
    List<WorkoutDto> getMyWorkouts(@CurrentSecurityContext(expression = "authentication")
                                   Authentication authentication) {
        return workoutService.getWorkouts(authentication);
    }

    @PostMapping("/custom")
    WorkoutDto addCustomWorkout(@CurrentSecurityContext(expression = "authentication")
                                Authentication authentication,
                                @RequestBody AddCustomWorkoutDto addCustomWorkoutDto
    ) {
        return workoutService.addCustomWorkout(authentication, addCustomWorkoutDto);
    }

    @PostMapping("/")
    WorkoutDto addWorkout(@CurrentSecurityContext(expression = "authentication")
                      Authentication authentication,
                      @RequestBody AddWorkoutDto addWorkoutDto
    ) {
        return workoutService.addWorkout(authentication, addWorkoutDto);
    }

    @PatchMapping("/")
    WorkoutDto editWorkout(@CurrentSecurityContext(expression = "authentication")
                      Authentication authentication,
                      @RequestBody EditWorkoutDto editWorkoutDto
    ) {
        return workoutService.editWorkout(authentication, editWorkoutDto);
    }

    @DeleteMapping("/")
    void deleteWorkout(@CurrentSecurityContext(expression = "authentication")
                           Authentication authentication,
                           @RequestBody DeleteWorkoutDto deleteWorkoutDto
    ) {
        workoutService.deleteWorkout(authentication, deleteWorkoutDto);
    }

}











