package app.activity;
import java.util.List;

import app.recipe.RecipeDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;


    @GetMapping("/search")
    List<Activity> searchActivity(@RequestParam String activity) {
        return activityService.searchActivity(activity);
    }














}
