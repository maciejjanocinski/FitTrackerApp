package app.product;

import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private BigDecimal protein;

    private BigDecimal fat;

    private BigDecimal carbohydrates;

    private BigDecimal fiber;

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

    static List<Product> parseProductsFromResponseDto(ResponseDTO response, String query, User user) {
        List<Product> products = new ArrayList<>();
        if (response == null) {
            return products;
        }
        for (HintDTO hint : response.getHints()) {
            FoodDTO food = hint.getFood();
            Map<String, BigDecimal> nutrients = food.getNutrients();



            Product product = new Product();
            checkIfFieldsAreNotNullAndSetValues(
                    product,
                    food.getFoodId(),
                    food.getLabel(),
                    nutrients.get("ENERC_KCAL"),
                    nutrients.get("PROCNT"),
                    nutrients.get("FAT"),
                    nutrients.get("CHOCDF"),
                    nutrients.get("FIBTG"),
                    food.getImage(),
                    query
            );

            List<Measure> measures = hint.getMeasures().stream().map(measureDto -> Measure.builder()
                    .name(measureDto.getLabel())
                    .weight(measureDto.getWeight())
                    .build()).collect(Collectors.toList());

            product.setUsed(false);
            product.setMeasures(measures);
            product.setUser(user);
            products.add(product);
        }
        return products;
    }


    static void checkIfFieldsAreNotNullAndSetValues(Product product,
                                                    String foodId,
                                                    String label,
                                                    BigDecimal kcal,
                                                    BigDecimal protein,
                                                    BigDecimal fat,
                                                    BigDecimal carbohydrates,
                                                    BigDecimal fiber,
                                                    String image,
                                                    String query
    ) {

        product.setProductId(valueOrEmpty(foodId));
        product.setName(valueOrEmpty(label));
        product.setKcal(valueOrZero(kcal));
        product.setProtein(valueOrZero(protein));
        product.setFat(valueOrZero(fat));
        product.setCarbohydrates(valueOrZero(carbohydrates));
        product.setFiber(valueOrZero(fiber));
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

