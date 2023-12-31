package app.goal;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalService;

    @GetMapping("/")
     GoalResponseDto getGoals(@CurrentSecurityContext(expression = "authentication")
                                                        Authentication authentication) {
        return goalService.getGoal(authentication);
    }

    @PostMapping("/custom")
     GoalResponseDto setCustomGoal(@CurrentSecurityContext(expression = "authentication")
                                                        Authentication authentication,
                                   @RequestBody AddCustomGoalDto goalsDto) {
        return goalService.setCustomGoal(authentication, goalsDto);
    }

    @PostMapping("/")
    GoalResponseDto setGoal(@CurrentSecurityContext(expression = "authentication")
                                  Authentication authentication,
                                  @RequestBody AddGoalDto addGoalDto) {
        return goalService.setGoal(authentication, addGoalDto);
    }
}
