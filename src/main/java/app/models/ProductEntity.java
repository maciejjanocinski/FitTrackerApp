package app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
@Data
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private String productId;

    private String name;

    private double kcal;

    private double protein;

    private double fat;

    private double carbohydrates;

    private double fiber;

    private String image;

    private boolean isUsed = false;

    @ElementCollection
    private Map<String, Double> measures;

}

