package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
public class NutrientsLeftToReachTodayGoals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private double kcal;
    private double protein;
    private double fat;
    private double carbohydrates;
    private double fiber;

    public NutrientsLeftToReachTodayGoals() {
        this.kcal = 0;
        this.protein = 0;
        this.fat = 0;
        this.carbohydrates = 0;
        this.fiber = 0;
    }

    public NutrientsLeftToReachTodayGoals(double kcal, double protein, double fat, double carbohydrates, double fiber) {
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
    }
}
