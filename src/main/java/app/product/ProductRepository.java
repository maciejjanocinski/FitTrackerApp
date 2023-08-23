package app.product;

import app.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.isUsed = false")
    void deleteNotUsedProducts();

    Optional<Product> findProductEntityByProductIdAndName(String id, String name);

    List<Product> findAllByQuery(String query);

}
