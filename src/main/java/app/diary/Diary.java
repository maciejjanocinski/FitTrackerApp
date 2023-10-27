package app.diary;

import app.exceptions.InvalidInputException;
import app.goal.GoalDto;
import app.goal.GoalMapper;
import app.goal.GoalResponseDto;
import app.goal.GoalValues;
import app.product.Product;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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


    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<ProductInDiary> products;

     void addProduct(ProductInDiary product) {
        this.products.add(product);
        calculateNutrientsLeft();
        calculateNutrientsSum();
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


    public Diary setGoal(GoalDto goalDto, GenderEnum.Gender gender) {
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

    GoalValues countGoal(GoalDto goalDto, GenderEnum.Gender gender) {
        BigDecimal protein = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.proteinPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal carbohydrates = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal fat = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.fatPercentage())).divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        BigDecimal fiber = Objects.equals(gender, GenderEnum.Gender.MALE) ? BigDecimal.valueOf(38) : BigDecimal.valueOf(25);

        return GoalValues.builder()
                .kcal(goalDto.kcal())
                .protein(protein)
                .carbohydrates(carbohydrates)
                .fat(fat)
                .fiber(fiber)
                .build();
    }

    void setGoalValuesToDiary(GoalValues goalValues) {
        this.setGoalKcal(goalValues.kcal());
        this.setGoalProtein(goalValues.protein());
        this.setGoalCarbohydrates(goalValues.carbohydrates());
        this.setGoalFat(goalValues.fat());
        this.setGoalFiber(goalValues.fiber());
    }

    ProductInDiary generateNewProductInDiary( Product product, String measureLabel, BigDecimal quantity) {
        String productId = product.getProductId();
        String productName = product.getName();
        BigDecimal calories = product.getKcal().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal proteins = product.getProtein().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal carbs = product.getCarbohydrates().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal fats = product.getFat().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel).multiply(quantity));
        BigDecimal fiber = product.getFiber().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
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
                .measureLabel(measureLabel)
                .quantity(quantity)
                .image(image);


        return productInDiary.build();
    }

    public Diary() {
        this.sumKcal = BigDecimal.ZERO;
        this.sumProtein = BigDecimal.ZERO;
        this.sumCarbohydrates = BigDecimal.ZERO;
        this.sumFat = BigDecimal.ZERO;
        this.sumFiber = BigDecimal.ZERO;
        this.leftKcal = BigDecimal.ZERO;
        this.leftProtein = BigDecimal.ZERO;
        this.leftCarbohydrates = BigDecimal.ZERO;
        this.leftFat = BigDecimal.ZERO;
        this.leftFiber = BigDecimal.ZERO;
        this.goalKcal = BigDecimal.ZERO;
        this.goalProtein = BigDecimal.ZERO;
        this.goalCarbohydrates = BigDecimal.ZERO;
        this.goalFat = BigDecimal.ZERO;
        this.goalFiber = BigDecimal.ZERO;
        this.products = new ArrayList<>();
    }
}