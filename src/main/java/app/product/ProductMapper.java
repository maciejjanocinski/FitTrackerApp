package app.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id")
    @Mapping(target = "isUsed", ignore = true)
    ProductDto mapToProductDto(Product product);
}
