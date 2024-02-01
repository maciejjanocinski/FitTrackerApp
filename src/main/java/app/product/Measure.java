package app.product;

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
    private Product product;

    public Measure(Measure measure) {
        this.label = measure.getLabel();
        this.weight = measure.getWeight();
    }

    public static List<Measure> map(List<Measure> measures, Product product) {
        return measures.stream()
                .map(measure -> {
                    Measure newMeasure = new Measure(measure);
                    newMeasure.setProduct(product);
                    return newMeasure;
                })
                .toList();
    }
}
