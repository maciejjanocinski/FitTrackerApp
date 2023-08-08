package app.repositories;

import app.models.NutrientsSum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientsSumRepository extends JpaRepository<NutrientsSum, Long> {

    @Query("SELECT new NutrientsSum (" +
            " COALESCE(SUM(p.kcal), 0), COALESCE(SUM(p.protein), 0), COALESCE(SUM(p.carbohydrates), 0), COALESCE(SUM(p.fat), 0), COALESCE(SUM(p.fiber), 0))" +
            " FROM ProductAddedToDiary p where p.diary.id = :diaryId")
    NutrientsSum getTotalNutrientsSum(@Param("diaryId")Long diaryId);


}
