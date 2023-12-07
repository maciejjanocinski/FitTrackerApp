package app;

import com.stripe.Stripe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FitTrackerAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitTrackerAppApplication.class, args);
        Stripe.apiKey = "sk_test_51OKQhgKu0TQyqhCeJYJ8H0kYLChtXsf3QgEBEYUdK18PTX2i9kewxffKovuAzFRS2MnfFXSZhCJN33542quMpIod00Jd57EvIT";
    }
}
