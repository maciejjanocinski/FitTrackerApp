package app.repository;

import app.models.UsedProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedProductsRepository extends JpaRepository<UsedProductsEntity, Long> {
}
