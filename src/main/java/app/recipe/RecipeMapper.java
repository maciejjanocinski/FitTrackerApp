package app.recipe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
     @Mapping(target = "used", ignore = true)
     @Mapping(target = "query", ignore = true)
     @Mapping(target = "id", ignore = true)
     Recipe mapToRecipe(RecipeDto recipeRequestDto);
}
