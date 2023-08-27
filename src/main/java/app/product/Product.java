package app.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String productId;

    private String name;

    private BigDecimal kcal;

    private BigDecimal protein;

    private BigDecimal fat;

    private BigDecimal carbohydrates;

    private BigDecimal fiber;

    private String image;

    private boolean isUsed = false;

    private String query;

    @ElementCollection
    @JsonIgnore
    private Map<String, BigDecimal> measures;
}

