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

import static app.product.Measure.map;
import static app.util.Utils.*;

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
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Nutrients nutrients;

    private String currentlyUsedMeasureName;

    private BigDecimal quantity;

    @Column(columnDefinition = "TEXT")
    private String image;

    private String query;
    private boolean lastlyAdded = false;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
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
                .kcal(valueOrZero(nutrients.get(KCAL_API)))
                .proteinGrams(valueOrZero(nutrients.get(PROTEIN_API)))
                .carbohydratesGrams(valueOrZero(nutrients.get(CARBS_API)))
                .fatGrams(valueOrZero(nutrients.get(FAT_API)))
                .fiberGrams(valueOrZero(nutrients.get(FIBER_API)))
                .product(this)
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
        return oldQuantity.multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
    }

    public Product(Product product) {
        this.name = product.getName();
        this.nutrients = new Nutrients(product.getNutrients());
        this.currentlyUsedMeasureName = product.getCurrentlyUsedMeasureName();
        this.quantity = product.getQuantity();
        this.image = product.getImage();
        this.query = product.getQuery();
        this.measures = map(product.getMeasures(), this);
        this.user = product.getUser();
        this.diary = product.getDiary();
    }
}

