package app.repository;

import app.models.NutrientsSum;
import app.models.ProductAddedToDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsAddedToDiaryRepository extends JpaRepository<ProductAddedToDiary, Long> {

}
