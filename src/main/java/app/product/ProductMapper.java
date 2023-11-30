package app.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

   static List<ProductDto> mapToProductDtoList(List<Product> products) {
        return products.stream()
                .map(ProductMapper::mapToProductDto)
                .toList();
    }

    static ProductDto mapToProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .kcal(product.getNutrients().getKcal())
                .protein(product.getNutrients().getProteinQuantityInGrams())
                .fat(product.getNutrients().getFatQuantityInGrams())
                .carbohydrates(product.getNutrients().getCarbohydratesQuantityInGrams())
                .fiber(product.getNutrients().getFiberQuantityInGrams())
                .currentlyUsedMeasure(product.getCurrentlyUsedMeasure().getName())
                .quantity(product.getQuantity())
                .image(product.getImage())
                .isUsed(product.isUsed())
                .query(product.getQuery())
                .measures(product.getMeasures())
                .build();
    }

    @Mapping(target = "id", ignore = true)
    static Product mapProductToProduct(Product product) {
        return Product.builder()
                .name(product.getName())
                .nutrients(product.getNutrients())
                .currentlyUsedMeasure(product.getCurrentlyUsedMeasure())
                .quantity(product.getQuantity())
                .image(product.getImage())
                .isUsed(product.isUsed())
                .query(product.getQuery())
                .measures(mapToMeasureList(product.getMeasures()))
                .build();
    }

    static Measure mapMeasureToMeasure(Measure measure) {
        return Measure.builder()
                .name(measure.getName())
                .weight(measure.getWeight())
                .build();
    }

    static List<Measure> mapToMeasureList(List<Measure> measures) {
        return measures.stream()
                .map(ProductMapper::mapMeasureToMeasure)
                .toList();
    }

    static Measure mapToMeasure(MeasureDto measureDto) {
        return Measure.builder()
                .name(measureDto.getLabel())
                .weight(measureDto.getWeight())
                .build();
    }

}
