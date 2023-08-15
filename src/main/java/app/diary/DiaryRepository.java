package app.diary;

import app.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DiaryRepository extends JpaRepository<Diary, Long> {
}
