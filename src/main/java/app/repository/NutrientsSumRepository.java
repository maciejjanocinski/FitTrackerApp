package app.repository;

import app.models.Diary;
import app.models.NutrientsSum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientsSumRepository extends JpaRepository<NutrientsSum, Long> {

    @Query("SELECT new NutrientsSum (" +
            " SUM(p.kcal), SUM(p.protein), SUM(p.carbohydrates), SUM(p.fat), SUM(p.fiber))" +
            " FROM ProductAddedToDiary p where p.diary.id = :diaryId")
    NutrientsSum getTotalNutrientsSum(@Param("diaryId")Long diaryId);

}
