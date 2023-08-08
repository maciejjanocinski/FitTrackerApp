package app.repositories;

import app.models.ProductAddedToDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsAddedToDiaryRepository extends JpaRepository<ProductAddedToDiary, Long> {
        ProductAddedToDiary findProductAddedToDiaryById(Long id);
        List<ProductAddedToDiary> findProductAddedToDiaryByProductName(String name);
}
