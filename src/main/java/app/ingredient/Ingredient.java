package app.ingredient;

import app.common.Product;
import app.diary.Diary;
import app.exceptions.InvalidInputException;
import app.nutrients.Nutrients;
import app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static app.ingredient.Measure.map;
import static app.util.Utils.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient extends Product {

    private String currentlyUsedMeasureName;

    private BigDecimal quantity;

    private boolean lastlyAdded = false;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "ingredient",
            orphanRemoval = true)
    private List<Measure> measures;

    static Measure searchForMeasure(List<Measure> measures, String measureLabel) {
        return measures.stream()
                .filter(m -> m.getLabel().equals(measureLabel))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(MEASURE_NOT_FOUND_MESSAGE));
    }

    void checkIfFieldsAreNotNullAndSetValues(
            String label,
            String image,
            String query
    ) {

        this.setName(valueOrEmpty(label));
        this.setImage(valueOrEmpty(image));
        this.setQuery(query);
    }


    Nutrients createNutrients(Map<String, BigDecimal> nutrients) {
        return Nutrients.builder()
                .kcal(valueOrZero(nutrients.get(KCAL_API)))
                .proteinGrams(valueOrZero(nutrients.get(PROTEIN_API)))
                .carbohydratesGrams(valueOrZero(nutrients.get(CARBS_API)))
                .fatGrams(valueOrZero(nutrients.get(FAT_API)))
                .fiberGrams(valueOrZero(nutrients.get(FIBER_API)))
                .ingredient(this)
                .build();
    }

    BigDecimal valueOrZero(BigDecimal numValue) {
        return numValue == null ? BigDecimal.ZERO : numValue;
    }

    String valueOrEmpty(String textValue) {
        return textValue == null ? "" : textValue;
    }

    public void changeAmount(String newMeasureLabel, BigDecimal newQuantity) {
        Measure newMeasure = searchForMeasure(measures, newMeasureLabel);
        Nutrients nutrients = super.getNutrients();

        BigDecimal newKcalQuantity = calcNewQuantity(nutrients.getKcal(), newQuantity, newMeasure);
        BigDecimal newProteinQuantity = calcNewQuantity(nutrients.getProteinGrams(), newQuantity, newMeasure);
        BigDecimal newCarbohydratesQuantity = calcNewQuantity(nutrients.getCarbohydratesGrams(), newQuantity, newMeasure);
        BigDecimal newFatQuantity = calcNewQuantity(nutrients.getFatGrams(), newQuantity, newMeasure);
        BigDecimal newFiberQuantity = calcNewQuantity(nutrients.getFiberGrams(), newQuantity, newMeasure);

        Nutrients newNutrients = Nutrients.builder()
                .kcal(newKcalQuantity)
                .proteinGrams(newProteinQuantity)
                .carbohydratesGrams(newCarbohydratesQuantity)
                .fatGrams(newFatQuantity)
                .fiberGrams(newFiberQuantity)
                .build();

        quantity = newQuantity;
        currentlyUsedMeasureName = newMeasure.getLabel();
        nutrients.map(newNutrients);
    }

    private BigDecimal calcNewQuantity(BigDecimal oldQuantity, BigDecimal newQuantity, Measure newMeasure) {
        Measure currentlyUsedMeasure = searchForMeasure(measures, currentlyUsedMeasureName);
        return oldQuantity.multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 1, RoundingMode.HALF_UP));
    }

    public Ingredient(Ingredient ingredient) {
        super(ingredient);
        this.currentlyUsedMeasureName = ingredient.getCurrentlyUsedMeasureName();
        this.quantity = ingredient.getQuantity();
        this.measures = map(ingredient.getMeasures(), this);
    }

    @Builder
    public Ingredient(Long id,
                      String name,
                      String image,
                      String query,
                      Nutrients nutrients,
                      String currentlyUsedMeasureName,
                      BigDecimal quantity,
                      boolean lastlyAdded,
                      List<Measure> measures,
                      User user,
                      Diary diary) {
        super(id, name, image, query, nutrients, user, diary);
        this.currentlyUsedMeasureName = currentlyUsedMeasureName;
        this.quantity = quantity;
        this.lastlyAdded = lastlyAdded;
        this.measures = measures;
    }
}

