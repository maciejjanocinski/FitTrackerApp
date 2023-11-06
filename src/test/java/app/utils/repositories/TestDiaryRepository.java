package app.utils.repositories;

import app.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TestDiaryRepository extends JpaRepository<Diary, Long> {
}
