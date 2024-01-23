package app.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SearchResult {
    private int from;
    private int to;
    private int count;
    private Links _links;
    private List<RecipeAndLinkDto> hits;

}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RecipeAndLinkDto {
    private RecipeApiResult recipe;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 public class RecipeApiResult {
    private Long id;
    private String label;
    private String image;
    private String source;
    private String url;
    private int yield;
    private BigDecimal caloriesPerServing;
    private BigDecimal proteinPerServing;
    private BigDecimal carbsPerServing;
    private BigDecimal fatPerServing;
    private BigDecimal fiberPerServing;
    private String query;
    private List<IngredientLineDto> ingredientLines;
    private Map<String, Nutrient> totalNutrients;
    void calculateNutrientsPerServing() {
        this.caloriesPerServing = totalNutrients.get("ENERC_KCAL").getQuantity().divide(BigDecimal.valueOf(yield), 2, RoundingMode.HALF_UP);
        this.proteinPerServing = totalNutrients.get("PROCNT").getQuantity().divide(BigDecimal.valueOf(yield), 2, RoundingMode.HALF_UP);
        this.carbsPerServing = totalNutrients.get("CHOCDF").getQuantity().divide(BigDecimal.valueOf(yield), 2, RoundingMode.HALF_UP);
        this.fatPerServing = totalNutrients.get("FAT").getQuantity().divide(BigDecimal.valueOf(yield), 2, RoundingMode.HALF_UP);
        this.fiberPerServing = totalNutrients.get("FIBTG").getQuantity().divide(BigDecimal.valueOf(yield), 2, RoundingMode.HALF_UP);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class Nutrient {
    private String label;
    private BigDecimal quantity;
    private String unit;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class Links {
    private Link next;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 class Link {
    private String title;
    private String href;
}
