package app.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Measure {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String label;

    private BigDecimal weight;

    @ManyToOne
    private Ingredient ingredient;

    public Measure(Measure measure) {
        this.label = measure.getLabel();
        this.weight = measure.getWeight();
    }

    public static List<Measure> map(List<Measure> measures, Ingredient ingredient) {
        return measures.stream()
                .map(measure -> {
                    Measure newMeasure = new Measure(measure);
                    newMeasure.setIngredient(ingredient);
                    return newMeasure;
                })
                .toList();
    }
}
