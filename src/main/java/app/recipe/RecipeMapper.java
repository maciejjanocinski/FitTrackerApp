package app.recipe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "diary", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    Recipe mapToRecipe(RecipeDto recipeRequestDto);


    static RecipeDto mapToRecipeDto(Recipe recipe) {
        return RecipeDto.builder()
                .id(recipe.getId())
                .label(recipe.getLabel())
                .image(recipe.getImage())
                .source(recipe.getSource())
                .url(recipe.getUrl())
                .yield(recipe.getYield())
                .ingredientLines(mapIngredientLineToIngredientLineDto(recipe.getIngredientLines()))
                .caloriesPerServing(recipe.getCaloriesPerServing().setScale(1, RoundingMode.HALF_UP))
                .proteinPerServing(recipe.getProteinPerServing().setScale(1, RoundingMode.HALF_UP))
                .carbsPerServing(recipe.getCarbsPerServing().setScale(1, RoundingMode.HALF_UP))
                .fatPerServing(recipe.getFatPerServing().setScale(1, RoundingMode.HALF_UP))
                .fiberPerServing(recipe.getFiberPerServing().setScale(1, RoundingMode.HALF_UP))
                .query(recipe.getQuery())
                .build();
    }

    static List<RecipeDto> mapRecipeDtoToRecipeDtoList(List<Recipe> recipes) {
        return recipes.stream()
                .map(RecipeMapper::mapToRecipeDto)
                .toList();
    }


    static List<IngredientLineDto> mapIngredientLineToIngredientLineDto(List<IngredientLine> ingredientLine) {
        return ingredientLine.stream()
                .map(i -> IngredientLineDto.builder()
                        .text(i.getText())
                        .build())
                .toList();

    }
}
