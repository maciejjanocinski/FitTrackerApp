package app.diary;

import app.goal.GoalDto;
import app.goal.GoalValues;
import app.product.Measure;
import app.product.Product;
import app.util.exceptions.InvalidInputException;
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

@Data
@AllArgsConstructor
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


    @OneToMany(
            mappedBy = "diary",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<ProductInDiary> productsInDiary;


     void addProduct(ProductInDiary product) {
        productsInDiary.add(product);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    void removeProduct(ProductInDiary product) {
        productsInDiary.remove(product);
        calculateNutrientsLeft();
        calculateNutrientsSum();
    }

    public void calculateNutrientsSum() {
        sumKcal = BigDecimal.valueOf(productsInDiary.stream().mapToDouble(p -> p.getKcal().doubleValue()).sum());
        sumProtein = BigDecimal.valueOf(productsInDiary.stream().mapToDouble(p -> p.getProtein().doubleValue()).sum());
        sumCarbohydrates = BigDecimal.valueOf(productsInDiary.stream().mapToDouble(p -> p.getCarbohydrates().doubleValue()).sum());
        sumFat = BigDecimal.valueOf(productsInDiary.stream().mapToDouble(p -> p.getFat().doubleValue()).sum());
        sumFiber = BigDecimal.valueOf(productsInDiary.stream().mapToDouble(p -> p.getFiber().doubleValue()).sum());
    }

    public void calculateNutrientsLeft() {
        leftKcal = goalKcal.subtract(sumKcal);
        leftProtein = goalProtein.subtract(sumProtein);
        leftCarbohydrates = goalCarbohydrates.subtract(sumCarbohydrates);
        leftFat = goalFat.subtract(sumFat);
        leftFiber = goalFiber.subtract(sumFiber);
    }


    public Diary setGoal(GoalDto goalDto, Gender gender) {
        validateGoalDto(goalDto);
        GoalValues goalValues = countGoal(goalDto, gender);
        setGoalValuesToDiary(goalValues);
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

    GoalValues countGoal(GoalDto goalDto, Gender gender) {
        BigDecimal protein = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.proteinPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal carbohydrates = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal fat = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.fatPercentage())).divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        BigDecimal fiber = Objects.equals(gender, Gender.MALE) ? BigDecimal.valueOf(38) : BigDecimal.valueOf(25);

        return GoalValues.builder()
                .kcal(goalDto.kcal())
                .protein(protein)
                .carbohydrates(carbohydrates)
                .fat(fat)
                .fiber(fiber)
                .build();
    }

    void setGoalValuesToDiary(GoalValues goalValues) {
        setGoalKcal(goalValues.kcal());
        setGoalProtein(goalValues.protein());
        setGoalCarbohydrates(goalValues.carbohydrates());
        setGoalFat(goalValues.fat());
        setGoalFiber(goalValues.fiber());
    }

   public ProductInDiary generateNewProductInDiary(Product product, String measureName, BigDecimal quantity) {

       Measure measure =  product.getMeasures().stream()
                 .filter(m -> m.getName().equals(measureName))
                 .findFirst()
                 .orElseThrow(() -> new InvalidInputException("Measure not found"));

        String productId = product.getProductId();
        String productName = product.getName();
        BigDecimal calories = product.getKcal().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(measure.getWeight()).multiply(quantity);
        BigDecimal proteins = product.getProtein().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(measure.getWeight()).multiply(quantity);
        BigDecimal carbs = product.getCarbohydrates().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(measure.getWeight()).multiply(quantity);
        BigDecimal fats = product.getFat().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(measure.getWeight()).multiply(quantity);
        BigDecimal fiber = product.getFiber().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(measure.getWeight()).multiply(quantity);
        String image = product.getImage();

        ProductInDiary.ProductInDiaryBuilder productInDiary = ProductInDiary.builder()
                .diary(this)
                .productId(productId)
                .productName(productName)
                .kcal(calories)
                .protein(proteins)
                .carbohydrates(carbs)
                .fat(fats)
                .fiber(fiber)
                .measureLabel(measure.getName())
                .quantity(quantity)
                .image(image);


        return productInDiary.build();
    }

    public Diary() {
        sumKcal = BigDecimal.ZERO;
        sumProtein = BigDecimal.ZERO;
        sumCarbohydrates = BigDecimal.ZERO;
        sumFat = BigDecimal.ZERO;
        sumFiber = BigDecimal.ZERO;
        leftKcal = BigDecimal.ZERO;
        leftProtein = BigDecimal.ZERO;
        leftCarbohydrates = BigDecimal.ZERO;
        leftFat = BigDecimal.ZERO;
        leftFiber = BigDecimal.ZERO;
        goalKcal = BigDecimal.ZERO;
        goalProtein = BigDecimal.ZERO;
        goalCarbohydrates = BigDecimal.ZERO;
        goalFat = BigDecimal.ZERO;
        goalFiber = BigDecimal.ZERO;
        productsInDiary = new ArrayList<>();
    }
}