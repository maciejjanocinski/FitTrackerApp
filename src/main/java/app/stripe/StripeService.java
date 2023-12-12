package app.stripe;

import app.authentication.Role;
import app.authentication.RoleRepository;
import app.user.User;
import app.user.UserRepository;
import app.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.swagger.v3.core.util.Json;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;
import static com.stripe.Stripe.apiKey;

@RequiredArgsConstructor
@Service
public class StripeService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @Value("${stripe.api.url}")
    private String stripeApiUrl;

    @Value("${stripe.price}")
    private String stripePrice;


    @Transactional
    public String getChecoutSession(Authentication authentication) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        User user = userService.getUserByUsername(authentication.getName());


        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl(frontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(frontendUrl + "/cancel")

                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(stripePrice)
                                        .build())

                        .build();
        Session session = Session.create(params);
        user.setStripeCheckoutSessionId(session.getId());
        return session.getUrl();
    }

    public String getBillingSession(Authentication authentication) throws JsonProcessingException {
        Stripe.apiKey = stripeApiKey;
        User user = userService.getUserByUsername(authentication.getName());

        String customerId = user.getStripeCustomerId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(stripeApiUrl)
                .queryParam("customer", customerId)
                .queryParam("return_url", frontendUrl);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            return jsonNode.get("url").asText();
        } else {
            return "Error during creating billing portal session";
        }
    }

    @PostMapping
    @Transactional
    public void handleWebhookEvent(@RequestBody WebhookData webhookData) {
        try {
            Stripe.apiKey = stripeApiKey;
            if ("checkout.session.completed".equals(webhookData.getType())) {
                String checkoutSessionId = webhookData.getData().getObject().getId();
                Optional<User> userOptional = userRepository.findByStripeCheckoutSessionId(checkoutSessionId);

                if (userOptional.isEmpty()) {
                    throw new UsernameNotFoundException("User not found");
                }

                User user = userOptional.get();
                user.setStripeCustomerId(webhookData.getData().getObject().getCustomer());
                user.setStripeSubscriptionId(webhookData.getData().getObject().getSubscription());
            } else if ("customer.subscription.deleted".equals(webhookData.getType())) {
                List<User> users = userRepository.findAll();
                String customerId = webhookData.getData().getObject().getCustomer();
                Optional<User> userOptional = userRepository.findByStripeCustomerId(customerId);

                if (userOptional.isEmpty()) {
                    throw new UsernameNotFoundException("User not found");
                }

                User user = userOptional.get();
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




