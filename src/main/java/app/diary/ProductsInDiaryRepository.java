package app.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
 interface ProductsInDiaryRepository extends JpaRepository<ProductInDiary, Long> {

  void deleteProductInDiaryById(Long productInDiaryId);
}
