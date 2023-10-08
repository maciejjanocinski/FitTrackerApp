package app.diary;

import app.diary.dto.ProductInDiaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diary", ignore = true)
    void mapToProductAddedToDiary(ProductInDiary source, @MappingTarget ProductInDiary destination);

    ProductInDiaryDto mapToProductInDiaryDto(ProductInDiary productInDiary);

}
