package app.bodymetrics;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/body-metrics")
 class BodyMetricsController {

    private final BodyMetricsService bodyMetricsService;

    @GetMapping("/")
    BodyMetricsDto getBodyMetrics(@CurrentSecurityContext(expression = "authentication")
                                  Authentication authentication) {
        return bodyMetricsService.getBodyMetrics(authentication);
    }

    @PostMapping("/")
    BodyMetricsDto updateBodyMetrics(@CurrentSecurityContext(expression = "authentication")
                                  Authentication authentication,
                                     @RequestBody @Valid BodyMetricsDto bodyMetricsDto) {
        return bodyMetricsService.updateBodyMetrics(authentication, bodyMetricsDto);
    }

}
