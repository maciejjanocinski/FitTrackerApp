package app.diary;

import app.common.Gender;
import app.exceptions.InvalidInputException;
import app.goal.AddCustomGoalDto;
import app.nutrients.Nutrients;
import app.product.Product;
import app.recipe.Recipe;
import app.user.User;
import app.workout.Workout;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static app.util.Utils.FIBER_FEMALE;
import static app.util.Utils.FIBER_MALE;

@Data
@AllArgsConstructor
@Builder
@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Nutrients sumNutrients;

    @OneToOne(cascade = CascadeType.ALL)
    private Nutrients leftNutrients;

    @OneToOne(cascade = CascadeType.ALL)
    private Nutrients goalNutrients;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "diary")
    private List<Product> products;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "diary")
    private List<Recipe> recipes;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "diary")
    private List<Workout> workouts;

    @ManyToOne
    private User user;

    private BigDecimal kcalBurned;

    private LocalDate date;


    public void addProduct(Product product) {
        products.add(product);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    void removeProduct(Product product) {
        products.remove(product);
        product.setDiary(null);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    public void calculateBurnedCalories() {
        kcalBurned = workouts.stream()
                .map(Workout::getKcalBurned)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void calculateNutrientsSum() {
        BigDecimal sumKcal = BigDecimal.ZERO;
        BigDecimal sumProtein = BigDecimal.ZERO;
        BigDecimal sumCarbohydrates = BigDecimal.ZERO;
        BigDecimal sumFat = BigDecimal.ZERO;
        BigDecimal sumFiber = BigDecimal.ZERO;

        for (Product product : products) {
            Nutrients productNutrients = product.getNutrients();
            sumKcal = sumKcal.add(productNutrients.getKcal());
            sumProtein = sumProtein.add(productNutrients.getProteinGrams());
            sumCarbohydrates = sumCarbohydrates.add(productNutrients.getCarbohydratesGrams());
            sumFat = sumFat.add(productNutrients.getFatGrams());
            sumFiber = sumFiber.add(productNutrients.getFiberGrams());
        }

        Nutrients newNutrients = Nutrients.builder()
                .kcal(sumKcal)
                .proteinGrams(sumProtein)
                .carbohydratesGrams(sumCarbohydrates)
                .fatGrams(sumFat)
                .fiberGrams(sumFiber)
                .build();


        sumNutrients.map(newNutrients);
    }

    public void calculateNutrientsLeft() {
        BigDecimal kcalLeft = goalNutrients.getKcal().subtract(sumNutrients.getKcal()).add(kcalBurned);
        BigDecimal proteinLeft = goalNutrients.getProteinGrams().subtract(sumNutrients.getProteinGrams());
        BigDecimal carbohydratesLeft = goalNutrients.getCarbohydratesGrams().subtract(sumNutrients.getCarbohydratesGrams());
        BigDecimal fatLeft = goalNutrients.getFatGrams().subtract(sumNutrients.getFatGrams());
        BigDecimal fiberLeft = goalNutrients.getFiberGrams().subtract(sumNutrients.getFiberGrams());

        Nutrients newNutrients = Nutrients.builder()
                .kcal(kcalLeft)
                .proteinGrams(proteinLeft)
                .carbohydratesGrams(carbohydratesLeft)
                .fatGrams(fatLeft)
                .fiberGrams(fiberLeft)
                .build();

        leftNutrients.map(newNutrients);
    }


    public void setCustomGoal(AddCustomGoalDto addCustomGoalDto, Gender gender) {
        validateGoalDto(addCustomGoalDto);
        Nutrients newGoalNutrients = countCustomGoal(addCustomGoalDto, gender);
        goalNutrients.map(newGoalNutrients);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    public void setGoal(Nutrients newGoalNutrients) {
        goalNutrients.map(newGoalNutrients);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    void validateGoalDto(AddCustomGoalDto addCustomGoalDto) {
        if (addCustomGoalDto.kcal().compareTo(BigDecimal.valueOf(0)) < 1 ||
                addCustomGoalDto.proteinPercentage() + addCustomGoalDto.carbohydratesPercentage() + addCustomGoalDto.fatPercentage() != 100) {
            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }
    }

    Nutrients countCustomGoal(AddCustomGoalDto addCustomGoalDto, Gender gender) {
        BigDecimal protein = addCustomGoalDto.kcal().multiply(BigDecimal.valueOf(addCustomGoalDto.proteinPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal carbohydrates = addCustomGoalDto.kcal().multiply(BigDecimal.valueOf(addCustomGoalDto.carbohydratesPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal fat = addCustomGoalDto.kcal().multiply(BigDecimal.valueOf(addCustomGoalDto.fatPercentage())).divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        BigDecimal fiber = Objects.equals(gender, Gender.MALE) ? BigDecimal.valueOf(FIBER_MALE) : BigDecimal.valueOf(FIBER_FEMALE);

        return Nutrients.builder()
                .kcal(addCustomGoalDto.kcal())
                .proteinGrams(protein)
                .carbohydratesGrams(carbohydrates)
                .fatGrams(fat)
                .fiberGrams(fiber)
                .build();
    }

    public Diary() {
        sumNutrients = new Nutrients();
        leftNutrients = new Nutrients();
        goalNutrients = new Nutrients();
        workouts = new ArrayList<>();
        products = new ArrayList<>();
        recipes = new ArrayList<>();
        kcalBurned = BigDecimal.ZERO;
        date = LocalDate.now();
    }
}