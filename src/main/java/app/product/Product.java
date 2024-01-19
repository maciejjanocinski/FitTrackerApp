package app.product;

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

import static app.nutrients.NutrientsMapper.mapNutrientsToNutrients;
import static app.product.Measure.mapToList;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    private Nutrients nutrients;

    private String currentlyUsedMeasureName;

    private BigDecimal quantity;

    @Column(columnDefinition = "TEXT")
    private String image;

    private String query;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "product",
            orphanRemoval = true)
    private List<Measure> measures;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "diary")
    private Diary diary;

    static Measure searchForMeasure(List<Measure> measures, String measureLabel) {
        return measures.stream()
                .filter(m -> m.getLabel().equals(measureLabel))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Measure not found"));
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
                .kcal(valueOrZero(nutrients.get("ENERC_KCAL")))
                .proteinGrams(valueOrZero(nutrients.get("PROCNT")))
                .carbohydratesGrams(valueOrZero(nutrients.get("CHOCDF")))
                .fatGrams(valueOrZero(nutrients.get("FAT")))
                .fiberGrams(valueOrZero(nutrients.get("FIBTG")))
                .product(this)
                .build();
    }

     BigDecimal valueOrZero(BigDecimal numValue) {
        return numValue == null ? BigDecimal.ZERO : numValue;
    }

    String valueOrEmpty(String textValue) {
        return textValue == null ? "" : textValue;
    }

    public void editProductAmount(String newMeasureLabel, BigDecimal newQuantity) {
        Measure newMeasure = searchForMeasure(measures, newMeasureLabel);
        Measure currentlyUsedMeasure = searchForMeasure(measures, currentlyUsedMeasureName);

        BigDecimal newKcalQuantity = nutrients.getKcal().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newProteinQuantity = nutrients.getProteinGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newCarbohydratesQuantity = nutrients.getCarbohydratesGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newFatQuantity = nutrients.getFatGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newFiberQuantity = nutrients.getFiberGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));

        Nutrients newNutrients = Nutrients.builder()
                .kcal(newKcalQuantity)
                .proteinGrams(newProteinQuantity)
                .carbohydratesGrams(newCarbohydratesQuantity)
                .fatGrams(newFatQuantity)
                .fiberGrams(newFiberQuantity)
                .product(this)
                .build();

        quantity = newQuantity;
        currentlyUsedMeasureName = newMeasure.getLabel();
        mapNutrientsToNutrients(nutrients, newNutrients);
    }

    public Product(Product product) {
        this.name = product.getName();
        this.nutrients = new Nutrients(product.getNutrients());
        this.currentlyUsedMeasureName = product.getCurrentlyUsedMeasureName();
        this.quantity = product.getQuantity();
        this.image = product.getImage();
        this.query = product.getQuery();
        this.measures = mapToList(product.getMeasures(), this);
        this.user = product.getUser();
        this.diary = product.getDiary();
    }
}

