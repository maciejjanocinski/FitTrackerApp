package app.diary;


import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final UserService userService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void resetDailyLog() {
        List<User> users = userService.getUsers();

        for (User user : users
        ) {
            Diary diary = user.getDiary();
            Diary newDiary = new Diary();
            newDiary.setUser(user);
            newDiary.getGoalNutrients().map(diary.getGoalNutrients());
            user.setDiary(newDiary);
        }
    }

}