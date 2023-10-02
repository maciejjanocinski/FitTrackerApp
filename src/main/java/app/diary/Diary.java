package app.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Diary {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    private BigDecimal sumKcal;
    private BigDecimal sumProtein;
    private BigDecimal sumCarbohydrates;
    private BigDecimal sumFat;
    private BigDecimal sumFiber;

    private BigDecimal goalKcal;
    private BigDecimal goalProtein;
    private BigDecimal goalFat;
    private BigDecimal goalCarbohydrates;
    private BigDecimal goalFiber;

    private BigDecimal leftKcal;
    private BigDecimal leftProtein;
    private BigDecimal leftFat;
    private BigDecimal leftCarbohydrates;
    private BigDecimal leftFiber;


    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<ProductInDiary> products;

    public void addProduct(ProductInDiary product) {
        this.products.add(product);
    }

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