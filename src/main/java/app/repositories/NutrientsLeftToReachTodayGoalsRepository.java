package app.repositories;

import app.models.NutrientsLeftToReachTodayGoals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientsLeftToReachTodayGoalsRepository extends JpaRepository<NutrientsLeftToReachTodayGoals, Long> {


    @Query("SELECT new NutrientsLeftToReachTodayGoals(" +
            "g.kcal - ns.totalKcal," +
            "g.protein - ns.totalProtein," +
            "g.carbohydrates - ns.totalCarbohydrates, " +
            "g.fat - ns.totalFat," +
            "g.fiber - ns.totalFiber) " +
            "FROM Goals g, NutrientsSum ns " +
            "where g.id = :goalsId and ns.id = :nutrientsSumId")
    NutrientsLeftToReachTodayGoals calculateNutrientsLeft(@Param("goalsId") Long goalsId, @Param("nutrientsSumId") Long nutrientsSumId);

}
