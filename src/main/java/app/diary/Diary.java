package app.diary;

import app.productAddedToDiary.ProductAddedToDiary;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Entity
public class Diary {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private double sumKcal;
    private double sumProtein;
    private double sumCarbohydrates;
    private double sumFat;
    private double sumFiber;

    private double goalKcal;
    private double goalProtein;
    private double goalFat;
    private double goalCarbohydrates;
    private double goalFiber;

    private double leftKcal;
    private double leftProtein;
    private double leftFat;
    private double leftCarbohydrates;
    private double leftFiber;


    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("diary")
    private List<ProductAddedToDiary> products;

     void calculateNutrientsSum() {
        this.sumKcal = this.products.stream().mapToDouble(ProductAddedToDiary::getKcal).sum();
        this.sumProtein = this.products.stream().mapToDouble(ProductAddedToDiary::getProtein).sum();
        this.sumCarbohydrates = this.products.stream().mapToDouble(ProductAddedToDiary::getCarbohydrates).sum();
        this.sumFat = this.products.stream().mapToDouble(ProductAddedToDiary::getFat).sum();
        this.sumFiber = this.products.stream().mapToDouble(ProductAddedToDiary::getFiber).sum();
    }

     void calculateNutrientsLeft() {
        this.leftKcal = this.goalKcal - this.sumKcal;
        this.leftProtein = this.goalProtein - this.sumProtein;
        this.leftCarbohydrates = this.goalCarbohydrates - this.sumCarbohydrates;
        this.leftFat = this.goalFat - this.sumFat;
        this.leftFiber = this.goalFiber - this.sumFiber;
    }

    public Diary() {
        this.sumKcal = 0;
        this.sumProtein = 0;
        this.sumCarbohydrates = 0;
        this.sumFat = 0;
        this.sumFiber = 0;

        this.goalKcal = 0;
        this.goalProtein = 0;
        this.goalCarbohydrates = 0;
        this.goalFat = 0;
        this.goalFiber = 0;

        this.leftKcal = 0;
        this.leftProtein = 0;
        this.leftCarbohydrates = 0;
        this.leftFat = 0;
        this.leftFiber = 0;
    }
}
