package app.recipe;

import app.nutrients.Nutrients;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String label;
    private String image;
    private String source;
    private String url;
    private int yield;
    private Nutrients nutrients = new Nutrients();
    private List<IngredientLineDto> ingredientLines;
    private Map<String, Nutrient> totalNutrients;

    void calculateNutrientsPerServing() {
        nutrients.setKcal(totalNutrients.get("ENERC_KCAL").getQuantity().divide(BigDecimal.valueOf(yield), 1, RoundingMode.HALF_UP));
        nutrients.setProteinGrams(totalNutrients.get("PROCNT").getQuantity().divide(BigDecimal.valueOf(yield), 1, RoundingMode.HALF_UP));
        nutrients.setFatGrams(totalNutrients.get("FAT").getQuantity().divide(BigDecimal.valueOf(yield), 1, RoundingMode.HALF_UP));
        nutrients.setCarbohydratesGrams(totalNutrients.get("CHOCDF").getQuantity().divide(BigDecimal.valueOf(yield), 1, RoundingMode.HALF_UP));
        nutrients.setFiberGrams(totalNutrients.get("FIBTG").getQuantity().divide(BigDecimal.valueOf(yield), 1, RoundingMode.HALF_UP));
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
