package app.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
 interface ProductAddedToDiaryRepository extends JpaRepository<ProductAddedToDiary, Long> {
   List<ProductAddedToDiary> findProductAddedToDiaryByProductName(String name);
}
