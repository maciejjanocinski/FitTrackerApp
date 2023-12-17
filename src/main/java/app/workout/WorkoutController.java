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
    Object addWorkout(@CurrentSecurityContext(expression = "authentication")
                      Authentication authentication,
                      @RequestBody AddWorkoutDto addWorkoutDto
    ) {
        return workoutService.addWorkout(authentication, addWorkoutDto);
    }

}











