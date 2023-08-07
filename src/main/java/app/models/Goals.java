package app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Data
@AllArgsConstructor
@Entity
public class Goals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private double kcal;
    private double protein;
    private double fat;
    private double carbohydrates;
    private double fiber;

    public Goals() {
        this.kcal = 0;
        this.protein = 0;
        this.fat = 0;
        this.carbohydrates = 0;
        this.fiber = 0;
    }

    public Goals(double kcal, double protein, double fat, double carbohydrates, double fiber) {
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
    }
}