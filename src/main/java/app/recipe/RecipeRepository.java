package app.recipe;

import app.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Recipe r WHERE r.isUsed = false AND r.user.id = :userId")
    void deleteNotFavouriteRecipes(@Param("userId") Long userId);

    List<Recipe> findAllByQueryAndUser(String query, User user);

    Optional<Recipe> findByIdAndUser(Long id, User user);
}
