package app.product;

import app.nutrients.Nutrients;
import org.mapstruct.Mapper;

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
                .currentlyUsedMeasure(product.getCurrentlyUsedMeasureName())
                .quantity(product.getQuantity())
                .image(product.getImage())
                .query(product.getQuery())
                .measures(product.getMeasures())
                .build();
    }

    static void mapProductToProduct(Product newProduct, Product product) {
        newProduct.setName(product.getName());
        newProduct.setNutrients(mapNutrientsToNutrients(product.getNutrients(), newProduct));
        newProduct.setCurrentlyUsedMeasureName(product.getCurrentlyUsedMeasureName());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setImage(product.getImage());
        newProduct.setQuery(product.getQuery());
        newProduct.setMeasures(mapMeasureListToMeasureList(product.getMeasures(), newProduct));
    }



    static List<Measure> mapMeasureListToMeasureList(List<Measure> measures, Product newProduct) {
        return measures.stream()
                .map(measure -> mapMeasureToMeasure(measure, newProduct))
                .toList();
    }

    static Measure mapMeasureToMeasure(Measure measure, Product newProduct) {
        return Measure.builder()
                .name(measure.getName())
                .weight(measure.getWeight())
                .product(newProduct)
                .build();
    }

    static Measure mapMeasureDtoToMeasure(MeasureDto measureDto, Product newProduct) {
        return Measure.builder()
                .name(measureDto.getLabel())
                .weight(measureDto.getWeight())
                .product(newProduct)
                .build();
    }

    static Nutrients mapNutrientsToNutrients(Nutrients nutrients, Product newProduct) {
        return Nutrients.builder()
                .kcal(nutrients.getKcal())
                .proteinQuantityInGrams(nutrients.getProteinQuantityInGrams())
                .fatQuantityInGrams(nutrients.getFatQuantityInGrams())
                .carbohydratesQuantityInGrams(nutrients.getCarbohydratesQuantityInGrams())
                .fiberQuantityInGrams(nutrients.getFiberQuantityInGrams())
                .product(newProduct)
                .build();
    }
}
