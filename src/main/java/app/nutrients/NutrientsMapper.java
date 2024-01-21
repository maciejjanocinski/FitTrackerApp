package app.nutrients;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NutrientsMapper {

    NutrientsMapper INSTANCE = Mappers.getMapper(NutrientsMapper.class);

    NutrientsDto mapToDto(Nutrients nutrients);

}
