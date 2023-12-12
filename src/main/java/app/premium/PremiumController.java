package app.premium;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/premium")
public class PremiumController {

    private final PremiumService premiumService;
    @GetMapping("/")
    String getPremium(@CurrentSecurityContext(expression = "authentication")
                      Authentication authentication) {
        return premiumService.getPremium(authentication);
    }

    @GetMapping("/confirm-credentials")
    String confirmCredentials(@CurrentSecurityContext(expression = "authentication")
                      Authentication authentication) {
        return premiumService.confirmCredentials(authentication);
    }
}
