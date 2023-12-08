package app.stripe;

import app.authentication.Role;
import app.authentication.RoleRepository;
import app.user.User;
import app.user.UserRepository;
import app.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.swagger.v3.core.util.Json;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;
import static com.stripe.Stripe.apiKey;

@RequiredArgsConstructor
@Service
public class StripeService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    User user;
    @Transactional
    public String getChecoutSession(Authentication authentication) throws StripeException {

        User user = userService.getUserByUsername(authentication.getName());

        String YOUR_DOMAIN = "http://localhost:4200";
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(YOUR_DOMAIN + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(YOUR_DOMAIN + "/cancel")

                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice("price_1OKhrLKu0TQyqhCeOQx8KRd4")
                                        .build())

                        .build();
        Session session = Session.create(params);
        user.setStripeCheckoutSessionId(session.getId());
        return session.getUrl();
    }

    public String getBillingSession(Authentication authentication) throws JsonProcessingException {
        User user = userService.getUserByUsername(authentication.getName());

        String stripeApiUrl = "https://api.stripe.com/v1/billing_portal/sessions";
        String customerId = user.getStripeCustomerId();
        String returnUrl = "http://localhost:4200";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(stripeApiUrl)
                .queryParam("customer", customerId)
                .queryParam("return_url", returnUrl);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            return jsonNode.get("url").asText();
        } else {
            return "Błąd podczas tworzenia sesji";
        }
    }

    @PostMapping
    @Transactional
    public void handleWebhookEvent(@RequestBody WebhookData webhookData) {
        try {
            if ("checkout.session.completed".equals(webhookData.getType())) {
                String checkoutSessionId = webhookData.getData().getObject().getId();

                List<User> users = userRepository.findAll();
                user = users.stream().filter(u -> Objects.equals(u.getStripeCheckoutSessionId(), checkoutSessionId)).findFirst().get();
                user.setStripeCustomerId(webhookData.getData().getObject().getCustomer());
                user.setStripeSubscriptionId(webhookData.getData().getObject().getSubscription());
            } else if("customer.subscription.deleted".equals(webhookData.getType())){
                List<User> users = userRepository.findAll();
                String customerId = webhookData.getData().getObject().getCustomer();

                user = users.stream().filter(u -> Objects.equals(u.getStripeCustomerId(), customerId)).findFirst().get();
                user.setStripeCustomerId(null);
                user.setStripeCheckoutSessionId(null);
                user.setStripeSubscriptionId(null);

                Role rolePremium = roleRepository.findByName(Role.roleType.ROLE_USER_PREMIUM.toString())
                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                user.removeRole(rolePremium);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

@Data
class WebhookData {
    private String type;
    private EventData data;
}

@Data
class EventData {
    private ObjectData object;

}

@Data
class ObjectData {
    private String id;
    private String customer;
    private String subscription;
}
