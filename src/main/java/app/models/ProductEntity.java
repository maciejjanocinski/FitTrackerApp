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
    @Column(name = "product_id")
    private String id;

    private String name;

    private double kcal;

    private double protein;

    private double fat;

    private double carbohydrates;

    private double fiber;

    private String image;

    @ElementCollection
    private Map<String, Double> measures;

}

