package app.recipe;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RecipeMapper {

    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);
    @Mapping(target = "name", source = "label")
    Recipe mapToRecipe(RecipeApiResult recipeRequestDto);

    List<RecipeDto> mapToDto(List<Recipe> recipes);

}
