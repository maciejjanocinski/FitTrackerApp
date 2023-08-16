package app.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

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

    private double kcal;

    private double protein;

    private double fat;

    private double carbohydrates;

    private double fiber;

    private String image;

    private boolean isUsed = false;

    private String query;

    @ElementCollection
    @JsonIgnore
    private Map<String, Double> measures;
}

