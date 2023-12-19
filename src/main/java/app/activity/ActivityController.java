package app.activity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
