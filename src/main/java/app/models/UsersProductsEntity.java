package app.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users_products")
public class UsersProductsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String productId;

    private double kcal;

    private double protein;

    private double fat;

    private double carbohydrates;

    private double fiber;

    private String image;

    private String measureLabel;

    private Double measureQuantity;

    private Double quantity;

}
