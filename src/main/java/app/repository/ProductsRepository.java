package app.repository;

import app.models.ProductEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductEntity p WHERE p.isUsed = false")
     void deleteNotUsedProducts();

     ProductEntity findProductEntityByProductIdAndName(String id, String name);
     ProductEntity findProductEntityByProductId(String id);

}
