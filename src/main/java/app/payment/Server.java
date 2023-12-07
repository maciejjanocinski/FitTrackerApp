package app.payment;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static com.stripe.Stripe.apiKey;

@RestController
@RequiredArgsConstructor
public class Server {

    @PostMapping("/create-checkout-session")
    public String createCheckoutSession() throws StripeException {



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
        return session.getUrl();
    }



    @PostMapping("/create-customer-portal-session")
    public String createBillingSession() throws JsonProcessingException {

        String stripeApiUrl = "https://api.stripe.com/v1/billing_portal/sessions";

        String customerId = "cus_P92uMFtsOeLxTR";
        String returnUrl = "http://localhost:4200";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(stripeApiUrl)
                .queryParam("customer", customerId)
                .queryParam("return_url", returnUrl);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(builder.toUriString(), HttpMethod.POST, request, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            return jsonNode.get("url").asText();
        } else {
            return "Błąd podczas tworzenia sesji";
        }
    }

}