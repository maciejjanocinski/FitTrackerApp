package app.productAddedToDiary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAddedToDiaryRepository extends JpaRepository<ProductAddedToDiary, Long> {
    Optional<List<ProductAddedToDiary>> findProductAddedToDiaryByProductName(String name);
}
