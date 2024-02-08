package app.recipe;

import app.common.Product;
import app.nutrients.Nutrients;
import app.ingredient.Ingredient;
import app.ingredient.Measure;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

import static app.util.Utils.PORTION_MEASURE;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Recipe extends Product {

    private String source;
    private String url;
    private int yield;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<IngredientLine> ingredientLines;

    Ingredient mapToIngredient() {
        return Ingredient.builder()
                .name(this.getName())
                .nutrients(new Nutrients(super.getNutrients()))
                .currentlyUsedMeasureName(PORTION_MEASURE)
                .quantity(BigDecimal.ONE)
                .image(this.getImage())
                .query(this.getQuery())
                .measures(List.of(Measure.builder()
                        .label("Portion")
                        .weight(BigDecimal.ONE)
                        .build()))
                .build();
    }

    @Builder
    public Recipe(Long id, String name, String image, String query, Nutrients nutrients, String source, String url, int yield, List<IngredientLine> ingredientLines) {
        super(id, name, image, query, nutrients);
        this.source = source;
        this.url = url;
        this.yield = yield;
        this.ingredientLines = ingredientLines;
    }
}
