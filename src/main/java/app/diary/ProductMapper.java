package app.diary;

import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    @Mapping(target = "id", ignore = true)
    ProductAddedToDiary ProductToProduct(ProductAddedToDiary productAddedToDiary);
}
