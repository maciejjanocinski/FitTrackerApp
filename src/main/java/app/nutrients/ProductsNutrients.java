package app.nutrients;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ProductsNutrients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Nutrient protein;

    @ManyToOne
    private Nutrient carbohydrates;

    @ManyToOne
    private Nutrient fat;

    @OneToOne(cascade = CascadeType.ALL)
    private NutrientsQuantity nutrientsQuantity;
}
