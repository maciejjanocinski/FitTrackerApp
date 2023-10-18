package app.diary;

import app.diary.dto.ProductInDiaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diary", ignore = true)
    void mapToProductInDiary(ProductInDiary source, @MappingTarget ProductInDiary destination);

    ProductInDiaryDto mapToProductInDiaryDto(ProductInDiary productInDiary);

}
