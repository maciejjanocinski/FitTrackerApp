package app.recipe;

import app.nutrients.Nutrients;
import app.nutrients.NutrientsDto;

import java.math.BigDecimal;
import java.util.List;

 public record RecipeDto(Long id,
                 String name,
                 String image,
                 String source,
                 String url,
                 int yield,
                 NutrientsDto nutrients,
                 String query,
                 List<IngredientLineDto> ingredientLines) {
}