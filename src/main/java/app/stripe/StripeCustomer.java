package app.stripe;

import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StripeCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;
    private String checkoutSessionId;
    private String subscriptionId;

    @OneToOne
    @JsonBackReference
    private User user;
}
