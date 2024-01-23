package app.stripe;

import app.roles.Role;
import app.roles.RoleRepository;
import app.roles.RoleType;
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
import jakarta.transaction.Transactional;
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

import java.util.Optional;

import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;
import static com.stripe.Stripe.apiKey;

@RequiredArgsConstructor
@Service
 class StripeService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final StripeCustomerRepository stripeCustomerRepository;
    private final RoleRepository roleRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.api.url}")
    private String stripeApiUrl;

    @Value("${stripe.price}")
    private String stripePrice;


    @Transactional
    public String getChecoutSession(Authentication authentication) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        StripeCustomer data = userService.getUserByUsername(authentication.getName()).getStripeCustomer();


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
        data.setCheckoutSessionId(session.getId());
        return session.getUrl();
    }

    public String getBillingSession(Authentication authentication) throws JsonProcessingException {
        Stripe.apiKey = stripeApiKey;
        StripeCustomer data = userService.getUserByUsername(authentication.getName()).getStripeCustomer();

        String customerId = data.getCustomerId();

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
                Optional<StripeCustomer> stripeDataOptional = stripeCustomerRepository.findByCheckoutSessionId(checkoutSessionId);

                if (stripeDataOptional.isEmpty()) {
                    throw new UsernameNotFoundException("User not found");
                }

                StripeCustomer data = stripeDataOptional.get();
                data.setCustomerId(webhookData.getData().getObject().getCustomer());
                data.setSubscriptionId(webhookData.getData().getObject().getSubscription());
            } else if ("customer.subscription.deleted".equals(webhookData.getType())) {
                String customerId = webhookData.getData().getObject().getCustomer();
                Optional<StripeCustomer> stripeDataOptional = stripeCustomerRepository.findByCustomerId(customerId);

                if (stripeDataOptional.isEmpty()) {
                    throw new UsernameNotFoundException("User not found");
                }

                StripeCustomer data = stripeDataOptional.get();
                data.setCustomerId(null);
                data.setCheckoutSessionId(null);
                data.setSubscriptionId(null);

                Role rolePremium = roleRepository.findByName(RoleType.ROLE_USER_PREMIUM.toString())
                        .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
                User user = data.getUser();
                user.removeRole(rolePremium);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}




