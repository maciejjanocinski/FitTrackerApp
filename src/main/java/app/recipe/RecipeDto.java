package app.recipe;

import java.math.BigDecimal;
import java.util.List;

 public record RecipeDto(Long id,
                 String label,
                 String image,
                 String source,
                 String url,
                 int yield,
                 BigDecimal caloriesPerServing,
                 BigDecimal proteinPerServing,
                 BigDecimal carbsPerServing,
                 BigDecimal fatPerServing,
                 BigDecimal fiberPerServing,
                 String query,
                 List<IngredientLineDto> ingredientLines) {
}