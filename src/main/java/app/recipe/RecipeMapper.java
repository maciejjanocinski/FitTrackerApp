package app.recipe;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RecipeMapper {

    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    Recipe mapToRecipe(RecipeApiResult recipeRequestDto);

    List<RecipeDto> mapToDto(List<Recipe> recipes);

}
