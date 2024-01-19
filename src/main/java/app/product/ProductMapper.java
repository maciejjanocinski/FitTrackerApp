package app.product;

import app.nutrients.Nutrients;
import app.recipe.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto mapToDto(Product product);

    @Mapping(target = "id", ignore = true)
    Product mapToProduct(Product product);

    List<ProductDto> mapToDto(List<Product> products);
}




























