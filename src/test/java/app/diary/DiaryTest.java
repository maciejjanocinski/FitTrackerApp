package app.diary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DiaryTest {
    Diary diary = new Diary();

    @BeforeEach
    void setUp() {
        this.diary = new Diary();
    }

    @Test
    void calculateNutrientsSum() {
        // given
        addProductsToDiary();

        // when
        diary.calculateNutrientsSum();

        // then
        assertEquals(BigDecimal.valueOf(500).setScale(1, RoundingMode.HALF_UP), diary.getSumKcal());
        assertEquals(BigDecimal.valueOf(50).setScale(1, RoundingMode.HALF_UP), diary.getSumProtein());
        assertEquals(BigDecimal.valueOf(100).setScale(1, RoundingMode.HALF_UP), diary.getSumCarbohydrates());
        assertEquals(BigDecimal.valueOf(150).setScale(1, RoundingMode.HALF_UP), diary.getSumFat());
        assertEquals(BigDecimal.valueOf(200).setScale(1, RoundingMode.HALF_UP), diary.getSumFiber());
    }

    @Test
    void calculateNutrientsLeft() {
        //given
        addProductsToDiary();
        setGoals();
        diary.calculateNutrientsSum();

        // when
        diary.calculateNutrientsLeft();

        // then
        assertEquals(BigDecimal.valueOf(500).setScale(1, RoundingMode.HALF_UP), diary.getLeftKcal());
        assertEquals(BigDecimal.valueOf(50).setScale(1, RoundingMode.HALF_UP), diary.getLeftProtein());
        assertEquals(BigDecimal.valueOf(100).setScale(1, RoundingMode.HALF_UP), diary.getLeftCarbohydrates());
        assertEquals(BigDecimal.valueOf(150).setScale(1, RoundingMode.HALF_UP), diary.getLeftFat());
        assertEquals(BigDecimal.valueOf(200).setScale(1, RoundingMode.HALF_UP), diary.getLeftFiber());
    }

    private void setGoals() {
        diary.setGoalKcal(BigDecimal.valueOf(1000));
        diary.setGoalProtein(BigDecimal.valueOf(100));
        diary.setGoalCarbohydrates(BigDecimal.valueOf(200));
        diary.setGoalFat(BigDecimal.valueOf(300));
        diary.setGoalFiber(BigDecimal.valueOf(400));
    }

    private void addProductsToDiary() {
        ProductAddedToDiary product = ProductAddedToDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(20))
                .fat(BigDecimal.valueOf(30))
                .fiber(BigDecimal.valueOf(40))
                .build();

        diary.getProducts().addAll(List.of(product, product, product, product, product));
    }
}