package app.stripe;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @GetMapping("/create-checkout-session")
    public String createCheckoutSession(@CurrentSecurityContext(expression = "authentication")
                                        Authentication authentication) throws StripeException {
        return stripeService.getChecoutSession(authentication);
    }

    @GetMapping("/create-customer-portal-session")
    public String createBillingSession(@CurrentSecurityContext(expression = "authentication")
                                       Authentication authentication) throws JsonProcessingException {

        return stripeService.getBillingSession(authentication);
    }

    @PostMapping("/webhook")
    public void reciveWebhook(@RequestBody WebhookData o) {
        stripeService.handleWebhookEvent(o);
    }

}