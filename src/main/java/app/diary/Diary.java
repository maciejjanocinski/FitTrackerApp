package app.diary;

import app.goal.GoalDto;
import app.nutrients.Nutrients;
import app.product.Product;
import app.recipe.Recipe;
import app.util.exceptions.InvalidInputException;
import app.workout.Workout;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static app.nutrients.NutrientsMapper.mapNutrientsToNutrients;

@Data
@AllArgsConstructor
@Builder
@Entity
public class Diary {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Nutrients nutrientsSum;

    @ManyToOne(cascade = CascadeType.ALL)
    private Nutrients nutrientsLeft;

    @ManyToOne(cascade = CascadeType.ALL)
    private Nutrients goalNutrients;


    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "diary")
    @JsonManagedReference
    private List<Product> products;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "diary")
    @JsonManagedReference
    private List<Recipe> recipes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "diary")
    @JsonManagedReference
    private List<Workout> workouts;


    public void addProduct(Product product) {
        products.add(product);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    void removeProduct(Product product) {
        products.remove(product);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    public void calculateNutrientsSum() {
        BigDecimal sumKcal = products.stream()
                .map(p -> p.getNutrients().getKcal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumProtein = products.stream()
                .map(p -> p.getNutrients().getProteinQuantityInGrams())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumCarbohydrates = products.stream()
                .map(p -> p.getNutrients().getCarbohydratesQuantityInGrams())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumFat = products.stream()
                .map(p -> p.getNutrients().getFatQuantityInGrams())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumFiber = products.stream()
                .map(p -> p.getNutrients().getFiberQuantityInGrams())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        nutrientsSum.setKcal(sumKcal);
        nutrientsSum.setProteinQuantityInGrams(sumProtein);
        nutrientsSum.setCarbohydratesQuantityInGrams(sumCarbohydrates);
        nutrientsSum.setFatQuantityInGrams(sumFat);
        nutrientsSum.setFiberQuantityInGrams(sumFiber);
    }

    public void calculateNutrientsLeft() {
        BigDecimal kcalLeft = goalNutrients.getKcal().subtract(nutrientsSum.getKcal());
        BigDecimal proteinLeft = goalNutrients.getProteinQuantityInGrams().subtract(nutrientsSum.getProteinQuantityInGrams());
        BigDecimal carbohydratesLeft = goalNutrients.getCarbohydratesQuantityInGrams().subtract(nutrientsSum.getCarbohydratesQuantityInGrams());
        BigDecimal fatLeft = goalNutrients.getFatQuantityInGrams().subtract(nutrientsSum.getFatQuantityInGrams());
        BigDecimal fiberLeft = goalNutrients.getFiberQuantityInGrams().subtract(nutrientsSum.getFiberQuantityInGrams());

        nutrientsLeft.setKcal(kcalLeft);
        nutrientsLeft.setProteinQuantityInGrams(proteinLeft);
        nutrientsLeft.setCarbohydratesQuantityInGrams(carbohydratesLeft);
        nutrientsLeft.setFatQuantityInGrams(fatLeft);
        nutrientsLeft.setFiberQuantityInGrams(fiberLeft);
    }


    public Diary setGoal(GoalDto goalDto, Gender gender) {
        validateGoalDto(goalDto);
        Nutrients newGoalNutrients = countGoal(goalDto, gender);
        mapNutrientsToNutrients(goalNutrients, newGoalNutrients);
        calculateNutrientsLeft();
        calculateNutrientsSum();
        return this;
    }

    void validateGoalDto(GoalDto goalDto) {
        if (goalDto.kcal().compareTo(BigDecimal.valueOf(0)) < 1 ||
                goalDto.proteinPercentage() + goalDto.carbohydratesPercentage() + goalDto.fatPercentage() != 100) {
            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }
    }

    Nutrients countGoal(GoalDto goalDto, Gender gender) {
        BigDecimal protein = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.proteinPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal carbohydrates = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal fat = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.fatPercentage())).divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        BigDecimal fiber = Objects.equals(gender, Gender.MALE) ? BigDecimal.valueOf(38) : BigDecimal.valueOf(25);

        return Nutrients.builder()
                .kcal(goalDto.kcal())
                .proteinQuantityInGrams(protein)
                .carbohydratesQuantityInGrams(carbohydrates)
                .fatQuantityInGrams(fat)
                .fiberQuantityInGrams(fiber)
                .build();
    }

    public Diary() {
        nutrientsSum = new Nutrients();
        nutrientsLeft = new Nutrients();
        goalNutrients = new Nutrients();
        products = new ArrayList<>();
    }
}