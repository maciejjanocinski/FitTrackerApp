package app.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    private Long id;
    private String label;
    private String image;
    private String source;
    private String url;
    private int yield;
    private List<IngredientLineDto> ingredientLines;
    private Map<String, Nutrient> totalNutrients;
    private double caloriesPerServing;
    private double proteinPerServing;
    private double carbsPerServing;
    private double fatPerServing;
    private double fiberPerServing;
    private boolean isUsed;
    private String query;

    void calculateNutrientsPerServing() {
        this.caloriesPerServing = totalNutrients.get("ENERC_KCAL").getQuantity() / yield;
        this.proteinPerServing = totalNutrients.get("PROCNT").getQuantity() / yield;
        this.carbsPerServing = totalNutrients.get("CHOCDF").getQuantity() / yield;
        this.fatPerServing = totalNutrients.get("FAT").getQuantity() / yield;
        this.fiberPerServing = totalNutrients.get("FIBTG").getQuantity() / yield;
    }
}