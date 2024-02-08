package app.ingredient;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IngredientMapper {
    IngredientMapper INSTANCE = Mappers.getMapper(IngredientMapper.class);

    IngredientDto mapToDto(Ingredient ingredient);
    List<IngredientDto> mapToDto(List<Ingredient> ingredients);
}




























