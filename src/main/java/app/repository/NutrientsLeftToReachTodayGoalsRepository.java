package app.repository;

import app.models.NutrientsLeftToReachTodayGoals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientsLeftToReachTodayGoalsRepository extends JpaRepository<NutrientsLeftToReachTodayGoals, Long> {


    @Query("SELECT new NutrientsLeftToReachTodayGoals(" +
            "g.kcal - ns.totalKcal," +
            "g.protein - ns.totalProtein," +
            "g.carbohydrates - ns.totalCarbohydrates, " +
            "g.fat - ns.totalFat," +
            "g.fiber - ns.totalFiber) " +
            "FROM Goals g, NutrientsSum ns ")
    NutrientsLeftToReachTodayGoals calculateNutrientsLeft();

}
