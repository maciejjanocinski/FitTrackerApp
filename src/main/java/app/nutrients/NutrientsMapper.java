package app.nutrients;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NutrientsMapper {
    static void mapNutrientsToNutrients(Nutrients oldNutrients, Nutrients newNutrients) {
        oldNutrients.setKcal(newNutrients.getKcal());
        oldNutrients.setProteinGrams(newNutrients.getProteinGrams());
        oldNutrients.setCarbohydratesGrams(newNutrients.getCarbohydratesGrams());
        oldNutrients.setFatGrams(newNutrients.getFatGrams());
        oldNutrients.setFiberGrams(newNutrients.getFiberGrams());
        oldNutrients.setProduct(newNutrients.getProduct());
    }
}
