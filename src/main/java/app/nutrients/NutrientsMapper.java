package app.nutrients;

import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface NutrientsMapper {
    static void mapNutrientsToNutrients(Nutrients oldNutrients, Nutrients newNutrients) {
        oldNutrients.setKcal(newNutrients.getKcal());
        oldNutrients.setProteinQuantityInGrams(newNutrients.getProteinQuantityInGrams());
        oldNutrients.setCarbohydratesQuantityInGrams(newNutrients.getCarbohydratesQuantityInGrams());
        oldNutrients.setFatQuantityInGrams(newNutrients.getFatQuantityInGrams());
        oldNutrients.setFiberQuantityInGrams(newNutrients.getFiberQuantityInGrams());
    }

}
