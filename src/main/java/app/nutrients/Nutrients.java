package app.nutrients;

import app.diary.Diary;
import app.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Data
@Builder
public class Nutrients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal kcal;
    private BigDecimal proteinQuantityInGrams;
    private BigDecimal carbohydratesQuantityInGrams;
    private BigDecimal fatQuantityInGrams;
    private BigDecimal fiberQuantityInGrams;

    @OneToOne(mappedBy = "nutrients", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Product product;

    public Nutrients() {
        kcal = BigDecimal.ZERO;
        proteinQuantityInGrams = BigDecimal.ZERO;
        carbohydratesQuantityInGrams = BigDecimal.ZERO;
        fatQuantityInGrams = BigDecimal.ZERO;
        fiberQuantityInGrams = BigDecimal.ZERO;
    }
}
