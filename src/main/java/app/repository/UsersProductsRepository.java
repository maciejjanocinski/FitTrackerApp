package app.repository;

import app.models.NutrientsSum;
import app.models.UsersProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersProductsRepository extends JpaRepository<UsersProductsEntity, Long> {
    @Query("SELECT" +
            " SUM(p.kcal) AS totalKcal," +
            " SUM(p.protein) AS totalProtein," +
            " SUM(p.carbohydrates) AS totalCarbohydrates," +
            " SUM(p.fat) AS totalFat," +
            " SUM(p.fiber) AS totalFiber" +
            " FROM UsersProductsEntity p")
    NutrientsSum sumNutrients();
}
