package app.product;

import jakarta.transaction.Transactional;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.diary = null AND p.user.id = :userId AND p.lastlyAdded = false")
    void deleteNotUsedProducts(@Param("userId") Long userId);

    Optional<Product> findProductById(Long id);

    List<Product> findAllByQuery(String query);

}
