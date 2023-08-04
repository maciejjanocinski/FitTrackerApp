package app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users_products")
@AllArgsConstructor
@NoArgsConstructor
public class UsersProductsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usersProductsId;

    private Long userId;

    private String productId;

    private double kcal;

    private double protein;

    private double fat;

    private double carbohydrates;

    private double fiber;

    private String image;

    private String measureLabel;

    private Double quantity;

    public UsersProductsEntity(Long userId, String productId, double kcal, double protein, double fat, double carbohydrates, double fiber, String image, String measureLabel, Double quantity) {
        this.userId = userId;
        this.productId = productId;
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
        this.image = image;
        this.measureLabel = measureLabel;
        this.quantity = quantity;
    }
}
