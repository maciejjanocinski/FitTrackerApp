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

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static app.workout.Workout.generateNewCustomWorkout;
import static app.workout.Workout.generateNewWorkout;
import static app.workout.WorkoutMapper.mapWorkoutListToWorkoutListDto;
import static app.workout.WorkoutMapper.mapWorkoutToWorkoutDto;

@Service
@RequiredArgsConstructor
 class WorkoutService {

    private final UserService userService;
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
        return mapWorkoutListToWorkoutListDto(diary.getWorkouts());
    }

    @Transactional
    public WorkoutDto addCustomWorkout(Authentication authentication,
                                       AddCustomWorkoutDto addCustomWorkoutDto) {
        Diary diary = userService.getUserByUsername(authentication.getName()).getDiary();
        Workout workout = generateNewCustomWorkout(addCustomWorkoutDto);
        workout.setDiary(diary);
        diary.getWorkouts().add(workout);
        return mapWorkoutToWorkoutDto(workout);
    }

    @Transactional
    public WorkoutDto addWorkout(Authentication authentication, AddWorkoutDto addWorkoutDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Double weight = user.getBodyMetrics().getWeight();
        Optional<Activity> activity = activityRepository.findActivityById(addWorkoutDto.activityid());
        if (activity.isEmpty()) {
            throw new RuntimeException("Activity not found.");
        }
        CaloriesBurnedApiResponse caloriesBurnedApiResponse = getCaloriesBurnedApiResponse(addWorkoutDto.activityid(), addWorkoutDto.activitymin(), weight);

        if (caloriesBurnedApiResponse.getStatusCode() != 200) {
            throw new RuntimeException("Something went wrong");
        }
        BigDecimal burnedCalorie = BigDecimal.valueOf(caloriesBurnedApiResponse.getData().getBurnedCalorie());
        Workout workout = generateNewWorkout(
                addWorkoutDto,
                activity.get(),
                burnedCalorie
        );

        workout.setDiary(diary);
        diary.getWorkouts().add(workout);
        diary.calculateBurnedCalories();
        diary.calculateNutrientsLeft();

        return mapWorkoutToWorkoutDto(workout);
    }

    @Transactional
    public WorkoutDto editWorkout(Authentication authentication, EditWorkoutDto editWorkoutDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Double weight = user.getBodyMetrics().getWeight();
        List<Workout> workouts = user.getDiary().getWorkouts();

        Workout workout = workouts.stream()
                .filter(w -> w.getId().equals(editWorkoutDto.id()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Workout not found"));


        CaloriesBurnedApiResponse caloriesBurnedApiResponse = getCaloriesBurnedApiResponse(workout.getActivityId(), editWorkoutDto.activitymin(), weight);
        BigDecimal burnedCalorie = BigDecimal.valueOf(caloriesBurnedApiResponse.getData().getBurnedCalorie());
        workout.setDurationInMinutes(editWorkoutDto.activitymin());
        workout.setKcalBurned(burnedCalorie);

        diary.calculateBurnedCalories();
        diary.calculateNutrientsLeft();

        return mapWorkoutToWorkoutDto(workout);
    }
    @Transactional
    public void deleteWorkout(Authentication authentication, DeleteWorkoutDto deleteWorkoutDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        List<Workout> workouts = user.getDiary().getWorkouts();
        Workout workout = workouts.stream()
                .filter(w -> w.getId().equals(deleteWorkoutDto.id()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        workouts.remove(workout);
        workout.setDiary(null);

        diary.calculateBurnedCalories();
        diary.calculateNutrientsLeft();
    }


    private CaloriesBurnedApiResponse getCaloriesBurnedApiResponse(String activityId, Double activityMin, Double weight) {
        ResponseEntity<CaloriesBurnedApiResponse> responseEntity =
                restTemplate.exchange(caloriesBurnedUrlBuilder(activityId, activityMin, weight), CaloriesBurnedApiResponse.class);
        return responseEntity.getBody();
    }

    private RequestEntity<Void> caloriesBurnedUrlBuilder(String activityId, Double activityMin, Double weight) {
        URI uri = UriComponentsBuilder.fromUriString(burnedCaloriesUrl)
                .queryParam("activityid", activityId)
                .queryParam("activitymin", activityMin)
                .queryParam("weight", weight)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", workoutsKey);
        headers.set("X-RapidAPI-Host", workoutsHost);

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }



}
















