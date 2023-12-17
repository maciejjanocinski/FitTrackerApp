package app.workout;

import app.activity.Activity;
import app.activity.ActivityRepository;
import app.diary.Diary;
import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static app.workout.Workout.generateNewCustomWorkout;
import static app.workout.Workout.generateNewWorkout;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final UserService userService;
    private final WorkoutMapper workoutMapper;
    private final ActivityRepository activityRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.activities.key}")
    private String workoutsKey;

    @Value("${api.activities.host}")
    private String workoutsHost;

    @Value("${api.burnedCalories.url}")
    private String burnedCaloriesUrl;

    List<WorkoutDto> getWorkouts(Authentication authentication) {
        Diary diary = userService.getUserByUsername(authentication.getName()).getDiary();
        return workoutMapper.mapWorkoutListToWorkoutListDto(diary.getWorkouts());
    }

    @Transactional
    public WorkoutDto addCustomWorkout(Authentication authentication,
                                       AddCustomWorkoutDto addCustomWorkoutDto) {
        Diary diary = userService.getUserByUsername(authentication.getName()).getDiary();
        Workout workout = generateNewCustomWorkout(addCustomWorkoutDto);
        workout.setDiary(diary);
        diary.getWorkouts().add(workout);
        return workoutMapper.mapWorkoutToWorkoutDto(workout);
    }

    @Transactional
    public Object addWorkout(Authentication authentication, AddWorkoutDto addWorkoutDto) {
        Diary diary = userService.getUserByUsername(authentication.getName()).getDiary();
        Optional<Activity> activity = activityRepository.findActivityById(addWorkoutDto.activityid());
        if (activity.isEmpty()) {
            throw new RuntimeException("Activity not found.");
        }
        CaloriesBurnedApiResponse caloriesBurnedApiResponse = getCaloriesBurnedApiResponse(addWorkoutDto);

        if (caloriesBurnedApiResponse.getStatus_code() != 200) {
            throw new RuntimeException("Something went wrong");
        }
        Double burnedCalorie = caloriesBurnedApiResponse.getData().getBurnedCalorie();

        Workout workout = generateNewWorkout(addWorkoutDto,
                activity.get(),
                burnedCalorie
        );

        workout.setDiary(diary);
        diary.getWorkouts().add(workout);
        return workout;
    }





    private CaloriesBurnedApiResponse getCaloriesBurnedApiResponse(AddWorkoutDto addWorkoutDto) {
        ResponseEntity<CaloriesBurnedApiResponse> responseEntity =
                restTemplate.exchange(caloriesBurnedUrlBuilder(addWorkoutDto), CaloriesBurnedApiResponse.class);
        return responseEntity.getBody();
    }





    private RequestEntity<Void> caloriesBurnedUrlBuilder(AddWorkoutDto addWorkoutDto) {
        URI uri = UriComponentsBuilder.fromUriString(burnedCaloriesUrl)
                .queryParam("activityid", addWorkoutDto.activityid())
                .queryParam("activitymin", addWorkoutDto.activitymin())
                .queryParam("weight", addWorkoutDto.weight())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", workoutsKey);
        headers.set("X-RapidAPI-Host", workoutsHost);

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }

}
















