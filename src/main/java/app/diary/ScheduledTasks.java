package app.diary;


import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final UserService userService;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void resetDailyLog() {
        User user = userService.getUserByUsername("Maciej");
        Diary diary = user.getDiary();
        user.getDiariesHistory().add(diary);

        Diary newDiary = new Diary();
        newDiary.setUser(user);
        newDiary.getGoalNutrients().mapNutrients(diary.getGoalNutrients());
        user.setDiary(newDiary);
    }

}