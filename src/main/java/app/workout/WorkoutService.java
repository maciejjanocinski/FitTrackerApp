package app.workout;

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

import static app.workout.Workout.generateNewCustomWorkout;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final UserService userService;
    private final WorkoutMapper workoutMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.workouts.url}")
    private String workoutsUrl;

    @Value("${api.workouts.key}")
    private String workoutsKey;

    @Value("${api.workouts.host}")
    private String workoutsHost;

    List<WorkoutDto> getWorkouts(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return workoutMapper.mapWorkoutListToWorkoutListDto(user.getWorkouts());
    }

    @Transactional
    public WorkoutDto addCustomWorkout(Authentication authentication,
                                       AddCustomWorkoutDto addCustomWorkoutDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Workout workout = generateNewCustomWorkout(addCustomWorkoutDto);
        workout.setUser(user);
        user.getWorkouts().add(workout);
        return workoutMapper.mapWorkoutToWorkoutDto(workout);
    }

    List<Workout> getAllWorkouts() {
        for(int i = 1; i <= 9; i++) {
         var workoutApiResponse = getWorkoutApiResponse(i);
         parseWorkoutsFromApiResponse(workoutApiResponse);
        }


    }

    private WorkoutApiResponse getWorkoutApiResponse(int intensitylevel) {
        ResponseEntity<WorkoutApiResponse> responseEntity = restTemplate.exchange(workoutUrlBuilder(intensitylevel), WorkoutApiResponse.class);
        return responseEntity.getBody();
    }

    private List<Activity> parseWorkoutsFromApiResponse(WorkoutApiResponse workoutApiResponse) {
        if(workoutApiResponse.getStatus_code() != 200) {
            throw new RuntimeException();
        }
       return workoutApiResponse.getData();
    }

    private RequestEntity<Void> workoutUrlBuilder(int intensitylevel) {
        URI uri = UriComponentsBuilder.fromUriString(workoutsUrl)
                .queryParam("intensitylevel", intensitylevel)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", workoutsKey);
        headers.set("X-RapidAPI-Host", workoutsHost);

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }

}
















