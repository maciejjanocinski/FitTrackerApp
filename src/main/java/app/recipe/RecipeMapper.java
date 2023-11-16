package app.recipe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
     @Mapping(target = "user", ignore = true)
     Recipe mapToRecipe(RecipeDto recipeRequestDto);

     RecipeDto mapToRecipeDto(Recipe recipe);
}
