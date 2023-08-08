package app.repositories;

import app.models.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Product, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.isUsed = false")
     void deleteNotUsedProducts();

     Product findProductEntityByProductIdAndName(String id, String name);
     Product findProductEntityByProductId(String id);

}
