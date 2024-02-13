package app.ingredient;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Ingredient p WHERE p.diary = null AND p.user.id = :userId AND p.lastlyAdded = false")
    void deleteNotUsedProducts(@Param("userId") Long userId);

    Optional<Ingredient> findProductById(Long id);

    List<Ingredient> findAllByQuery(String query);

}
