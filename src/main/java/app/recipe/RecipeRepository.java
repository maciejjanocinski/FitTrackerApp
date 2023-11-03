package app.recipe;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Recipe r WHERE r.isUsed = false")
    void deleteNotFavouriteRecipes();

    List<Recipe> findAllByQuery(String query);
}
