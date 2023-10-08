package app.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String productId;

    private String name;

    private BigDecimal kcal;

    private BigDecimal protein;

    private BigDecimal fat;

    private BigDecimal carbohydrates;

    private BigDecimal fiber;

    private String image;

    private boolean isUsed = false;

    private String query;

    @ElementCollection
    @JsonIgnore
    @JsonBackReference
    private Map<String, BigDecimal> measures;


    public Product(
            Long id,
            String foodId,
            String name,
            BigDecimal bigDecimal,
            BigDecimal bigDecimal1,
            BigDecimal bigDecimal2,
            BigDecimal bigDecimal3,
            BigDecimal bigDecimal4,
            String image,
            String query,
            boolean b,
            Map<String, BigDecimal> measures
    ) {
                this.id = id;
                this.productId = foodId;
                this.name = name;
                this.kcal = bigDecimal;
                this.protein = bigDecimal1;
                this.fat = bigDecimal2;
                this.carbohydrates = bigDecimal3;
                this.fiber = bigDecimal4;
                this.image = image;
                this.query = query;
                this.isUsed = b;
                this.measures = measures;
    }
}

