package app.diary;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonBackReference
    private List<ProductAddedToDiary> products;

    public void calculateNutrientsSum() {
        this.sumKcal = this.products.stream().mapToDouble(ProductAddedToDiary::getKcal).sum();
        this.sumProtein = this.products.stream().mapToDouble(ProductAddedToDiary::getProtein).sum();
        this.sumCarbohydrates = this.products.stream().mapToDouble(ProductAddedToDiary::getCarbohydrates).sum();
        this.sumFat = this.products.stream().mapToDouble(ProductAddedToDiary::getFat).sum();
        this.sumFiber = this.products.stream().mapToDouble(ProductAddedToDiary::getFiber).sum();
    }

    public void calculateNutrientsLeft() {
        this.leftKcal = this.goalKcal - this.sumKcal;
        this.leftProtein = this.goalProtein - this.sumProtein;
        this.leftCarbohydrates = this.goalCarbohydrates - this.sumCarbohydrates;
        this.leftFat = this.goalFat - this.sumFat;
        this.leftFiber = this.goalFiber - this.sumFiber;
    }
}
