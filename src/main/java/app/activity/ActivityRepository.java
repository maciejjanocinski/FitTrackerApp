package app.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

    Optional<Activity> findActivityById(String id);

    List<Activity> findActivityByDescriptionContainingIgnoreCase(String description);

}
