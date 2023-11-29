package app.product;

import app.nutrients.Nutrient;
import app.nutrients.NutrientsQuantity;
import app.nutrients.ProductsNutrients;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;

    private String name;

    private BigDecimal kcal;

    @OneToOne(cascade = CascadeType.ALL)
    private ProductsNutrients productsNutrients;

    private String image;

    private boolean isUsed;

    private String query;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<Measure> measures;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    static List<Product> parseProductsFromResponseDto(ResponseDto response,
                                                      String query,
                                                      User user,
                                                      Nutrient protein,
                                                      Nutrient carbohydrates,
                                                      Nutrient fat) {
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
                    food.getFoodId(),
                    food.getLabel(),
                    food.getImage(),
                    query
            );

            List<Measure> measures = hint.getMeasures().stream().map(measureDto -> Measure.builder()
                    .name(measureDto.getLabel())
                    .weight(measureDto.getWeight())
                    .build())
                    .collect(Collectors.toList());

            ProductsNutrients productsNutrients = createProductsNutrients(
                    protein,
                    carbohydrates,
                    fat,
                    nutrients
            );

            product.setProductsNutrients(productsNutrients);
            product.setUsed(false);
            product.setMeasures(measures);
            product.setUser(user);
            products.add(product);
        }
        return products;
    }

    private static ProductsNutrients createProductsNutrients(Nutrient protein, Nutrient carbohydrates, Nutrient fat, Map<String, BigDecimal> nutrients) {
        NutrientsQuantity nutrientsQuantity = NutrientsQuantity.builder()
                .kcal(valueOrZero(nutrients.get("ENERC_KCAL")))
                .proteinQuantity(valueOrZero(nutrients.get("PROCNT")))
                .carbohydratesQuantity(valueOrZero(nutrients.get("CHOCDF")))
                .fatQuantity(valueOrZero(nutrients.get("FAT")))
                .fiber(valueOrZero(nutrients.get("FIBTG")))
                .weightInGrams(BigDecimal.valueOf(100))
                .build();

        return ProductsNutrients.builder()
                .protein(protein)
                .carbohydrates(carbohydrates)
                .fat(fat)
                .nutrientsQuantity(nutrientsQuantity)
                .build();
    }

    private static void checkIfFieldsAreNotNullAndSetValues(Product product,
                                                    String foodId,
                                                    String label,
                                                    String image,
                                                    String query
    ) {

        product.setProductId(valueOrEmpty(foodId));
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
}

