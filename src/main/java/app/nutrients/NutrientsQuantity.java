package app.nutrients;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutrientsQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal kcal;
    private BigDecimal proteinQuantity;
    private BigDecimal carbohydratesQuantity;
    private BigDecimal fatQuantity;
    private BigDecimal fiber;
    private BigDecimal weightInGrams;
}
