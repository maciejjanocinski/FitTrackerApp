package app.repository;

import app.models.UsersProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersProductsRepository extends JpaRepository<UsersProductsEntity, Long> {
}
