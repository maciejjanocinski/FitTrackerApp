package app.nutrients;

import app.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@AllArgsConstructor
@Data
@Builder
public class Nutrients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal kcal;
    private BigDecimal proteinGrams;
    private BigDecimal carbohydratesGrams;
    private BigDecimal fatGrams;
    private BigDecimal fiberGrams;

    @OneToOne
    private Product product;

    public Nutrients() {
        kcal = BigDecimal.ZERO;
        proteinGrams = BigDecimal.ZERO;
        carbohydratesGrams = BigDecimal.ZERO;
        fatGrams = BigDecimal.ZERO;
        fiberGrams = BigDecimal.ZERO;
    }

    public Nutrients(Nutrients nutrients) {
        this.kcal = nutrients.getKcal();
        this.proteinGrams = nutrients.getProteinGrams();
        this.carbohydratesGrams = nutrients.getCarbohydratesGrams();
        this.fatGrams = nutrients.getFatGrams();
        this.fiberGrams = nutrients.getFiberGrams();
    }

}
