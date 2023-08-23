package app.diary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diary", ignore = true)
    void mapToProductAddedToDiary(ProductAddedToDiary source, @MappingTarget ProductAddedToDiary destination);
    ProductAddedToDiaryDto mapToProductAddedToDiaryDto(ProductAddedToDiary productAddedToDiary);

}
