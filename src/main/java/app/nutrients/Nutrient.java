package app.nutrients;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Nutrient extends NutrientAbstract{
    @Id
    private Long id;
    private String name;
    private int kcalPerGram;
    @ManyToOne
    private ProductsNutrients productsNutrients;
}
