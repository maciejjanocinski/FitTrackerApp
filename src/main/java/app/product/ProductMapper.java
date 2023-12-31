package app.product;

import app.nutrients.Nutrients;
import app.recipe.Recipe;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                .kcal(product.getNutrients().getKcal().setScale(1, RoundingMode.HALF_UP))
                .protein(product.getNutrients().getProteinQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .fat(product.getNutrients().getFatQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .carbohydrates(product.getNutrients().getCarbohydratesQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .fiber(product.getNutrients().getFiberQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
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

    static Product mapRecipeToProduct(Recipe recipe) {
        return Product.builder()
                .name(recipe.getLabel())
                .nutrients(Nutrients.builder()
                        .kcal(recipe.getCaloriesPerServing())
                        .proteinQuantityInGrams(recipe.getProteinPerServing())
                        .fatQuantityInGrams(recipe.getCarbsPerServing())
                        .carbohydratesQuantityInGrams(recipe.getFatPerServing())
                        .fiberQuantityInGrams(recipe.getFiberPerServing())
                        .build()
                )
                .currentlyUsedMeasureName("Portion")
                .quantity(BigDecimal.ONE)
                .image(recipe.getImage())
                .query(recipe.getQuery())
                .measures(List.of(Measure.builder()
                        .name("Portion")
                        .weight(BigDecimal.valueOf(100))
                        .build()))
                .build();

    }
}




























