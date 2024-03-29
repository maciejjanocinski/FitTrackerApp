package app.nutrients;

import app.ingredient.Ingredient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    private BigDecimal proteinGrams;
    private BigDecimal carbohydratesGrams;
    private BigDecimal fatGrams;
    private BigDecimal fiberGrams;

    @OneToOne
    private Ingredient ingredient;

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

    public void map(Nutrients nutrients) {
        this.kcal = nutrients.getKcal();
        this.proteinGrams = nutrients.getProteinGrams();
        this.carbohydratesGrams = nutrients.getCarbohydratesGrams();
        this.fatGrams = nutrients.getFatGrams();
        this.fiberGrams = nutrients.getFiberGrams();
    }


}
