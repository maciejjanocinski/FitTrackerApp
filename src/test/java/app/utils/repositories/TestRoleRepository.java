package app.utils.repositories;

import app.authentication.Role;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TestRoleRepository extends JpaRepository<Role, Long> {
}
