package app.recipe;

import app.diary.Diary;
import app.nutrients.Nutrients;
import app.product.Measure;
import app.product.Product;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static app.util.Utils.POTION_MEASURE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    @Column(columnDefinition = "TEXT")
    private String image;
    private String source;
    private String url;
    private int yield;
    private BigDecimal caloriesPerServing;
    private BigDecimal proteinPerServing;
    private BigDecimal carbsPerServing;
    private BigDecimal fatPerServing;
    private BigDecimal fiberPerServing;
    private String query;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<IngredientLine> ingredientLines;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "diary")
    private Diary diary;


    Product mapToProduct() {
        return Product.builder()
                .name(this.getLabel())
                .nutrients(Nutrients.builder()
                        .kcal(this.getCaloriesPerServing())
                        .proteinGrams(this.getProteinPerServing())
                        .fatGrams(this.getCarbsPerServing())
                        .carbohydratesGrams(this.getFatPerServing())
                        .fiberGrams(this.getFiberPerServing())
                        .build()
                )
                .currentlyUsedMeasureName(POTION_MEASURE)
                .quantity(BigDecimal.ONE)
                .image(this.getImage())
                .query(this.getQuery())
                .measures(List.of(Measure.builder()
                        .label("Portion")
                        .weight(BigDecimal.ONE)
                        .build()))
                .build();
    }
}
