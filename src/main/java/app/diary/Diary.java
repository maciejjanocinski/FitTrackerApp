package app.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Diary {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long diaryId;

    private BigDecimal sumKcal = BigDecimal.ZERO;
    private BigDecimal sumProtein = BigDecimal.ZERO;
    private BigDecimal sumCarbohydrates = BigDecimal.ZERO;
    private BigDecimal sumFat = BigDecimal.ZERO;
    private BigDecimal sumFiber = BigDecimal.ZERO;

    private BigDecimal goalKcal = BigDecimal.ZERO;
    private BigDecimal goalProtein = BigDecimal.ZERO;
    private BigDecimal goalFat = BigDecimal.ZERO;
    private BigDecimal goalCarbohydrates = BigDecimal.ZERO;
    private BigDecimal goalFiber = BigDecimal.ZERO;

    private BigDecimal leftKcal = BigDecimal.ZERO;
    private BigDecimal leftProtein = BigDecimal.ZERO;
    private BigDecimal leftFat = BigDecimal.ZERO;
    private BigDecimal leftCarbohydrates = BigDecimal.ZERO;
    private BigDecimal leftFiber = BigDecimal.ZERO;


    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProductAddedToDiary> products;

    public void calculateNutrientsSum() {
        this.sumKcal = BigDecimal.valueOf(this.products.stream().mapToDouble(p -> p.getKcal().doubleValue()).sum());
        this.sumProtein = BigDecimal.valueOf(this.products.stream().mapToDouble(p -> p.getProtein().doubleValue()).sum());
        this.sumCarbohydrates = BigDecimal.valueOf(this.products.stream().mapToDouble(p -> p.getCarbohydrates().doubleValue()).sum());
        this.sumFat = BigDecimal.valueOf(this.products.stream().mapToDouble(p -> p.getFat().doubleValue()).sum());
        this.sumFiber = BigDecimal.valueOf(this.products.stream().mapToDouble(p -> p.getFiber().doubleValue()).sum());
    }

    public void calculateNutrientsLeft() {
        this.leftKcal = this.goalKcal.subtract(this.sumKcal);
        this.leftProtein = this.goalProtein.subtract(this.sumProtein);
        this.leftCarbohydrates = this.goalCarbohydrates.subtract(this.sumCarbohydrates);
        this.leftFat = this.goalFat.subtract(this.sumFat);
        this.leftFiber = this.goalFiber.subtract(this.sumFiber);
    }
}
