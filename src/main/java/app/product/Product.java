package app.product;

import app.diary.Diary;
import app.nutrients.Nutrients;
import app.user.User;
import app.util.exceptions.InvalidInputException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static app.nutrients.NutrientsMapper.mapNutrientsToNutrients;
import static app.product.ProductMapper.mapMeasureDtoToMeasure;

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
    @JsonManagedReference
    private Nutrients nutrients;

    private String currentlyUsedMeasureName;

    private BigDecimal quantity;

    private String image;

    private String query;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "product",
            orphanRemoval = true)
    @JsonManagedReference
    private List<Measure> measures;

    @ManyToOne
    @JsonBackReference
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "diary")
    private Diary diary;

    static List<Product> parseProductsFromResponseDto(ResponseDto response,
                                                      String query,
                                                      User user) {
        if (response == null) {
            return Collections.emptyList();
        }
        List<Product> products = new ArrayList<>();

        for (HintDto hint : response.getHints()) {
            FoodDto food = hint.getFood();
            Map<String, BigDecimal> nutrients = food.getNutrients();


            Product product = new Product();
            checkIfFieldsAreNotNullAndSetValues(
                    product,
                    food.getLabel(),
                    food.getImage(),
                    query
            );

            List<Measure> measures = hint.getMeasures().stream()
                    .map(measureDto -> mapMeasureDtoToMeasure(measureDto, product))
                    .toList();

            Nutrients nutrientsForProduct = createNutrients(nutrients);
            nutrientsForProduct.setProduct(product);

            Measure usedMeasure = searchForMeasure(measures, "Gram");

            product.setCurrentlyUsedMeasureName(usedMeasure.getName());
            product.setQuantity(BigDecimal.valueOf(100));
            product.setNutrients(nutrientsForProduct);
            product.setMeasures(measures);
            product.setUser(user);
            products.add(product);
        }
        return products;
    }


    private static Nutrients createNutrients(Map<String, BigDecimal> nutrients) {
        return Nutrients.builder()
                .kcal(valueOrZero(nutrients.get("ENERC_KCAL")))
                .proteinQuantityInGrams(valueOrZero(nutrients.get("PROCNT")))
                .carbohydratesQuantityInGrams(valueOrZero(nutrients.get("CHOCDF")))
                .fatQuantityInGrams(valueOrZero(nutrients.get("FAT")))
                .fiberQuantityInGrams(valueOrZero(nutrients.get("FIBTG")))
                .build();
    }

    private static Measure searchForMeasure(List<Measure> measures, String measureLabel) {
        return measures.stream()
                .filter(m -> m.getName().equals(measureLabel))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Measure not found"));
    }

    private static void checkIfFieldsAreNotNullAndSetValues(Product product,
                                                            String label,
                                                            String image,
                                                            String query
    ) {

        product.setName(valueOrEmpty(label));
        product.setImage(valueOrEmpty(image));
        product.setQuery(query);
    }

    static BigDecimal valueOrZero(BigDecimal numValue) {
        return numValue == null ? BigDecimal.ZERO : numValue;
    }

    static String valueOrEmpty(String textValue) {
        return textValue == null ? "" : textValue;
    }

    public void editProductAmount(String newMeasureLabel, BigDecimal newQuantity) {
        Measure newMeasure = searchForMeasure(measures, newMeasureLabel);
        Measure currentlyUsedMeasure = searchForMeasure(measures, currentlyUsedMeasureName);

        BigDecimal newKcalQuantity = nutrients.getKcal().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newProteinQuantity = nutrients.getProteinQuantityInGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newCarbohydratesQuantity = nutrients.getCarbohydratesQuantityInGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newFatQuantity = nutrients.getFatQuantityInGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));
        BigDecimal newFiberQuantity = nutrients.getFiberQuantityInGrams().multiply(newQuantity.multiply(newMeasure.getWeight()).divide(quantity.multiply(currentlyUsedMeasure.getWeight()), 2, RoundingMode.HALF_UP));

        Nutrients newNutrients = Nutrients.builder()
                .kcal(newKcalQuantity)
                .proteinQuantityInGrams(newProteinQuantity)
                .carbohydratesQuantityInGrams(newCarbohydratesQuantity)
                .fatQuantityInGrams(newFatQuantity)
                .fiberQuantityInGrams(newFiberQuantity)
                .product(this)
                .build();

        quantity = newQuantity;
        currentlyUsedMeasureName = newMeasure.getName();
        mapNutrientsToNutrients(nutrients, newNutrients);
    }


}

