package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
public class NutrientsSum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private double totalKcal;
    private double totalProtein;
    private double totalCarbohydrates;
    private double totalFat;
    private double totalFiber;


    public NutrientsSum(Double totalKcal, Double totalProtein, Double totalCarbohydrates, Double totalFat, Double totalFiber) {
        this.totalKcal = totalKcal;
        this.totalProtein = totalProtein;
        this.totalCarbohydrates = totalCarbohydrates;
        this.totalFat = totalFat;
        this.totalFiber = totalFiber;
    }

    public NutrientsSum() {
        this.totalKcal = 0.0;
        this.totalProtein = 0.0;
        this.totalCarbohydrates = 0.0;
        this.totalFat = 0.0;
        this.totalFiber = 0.0;
    }
}