package app.activity;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.activities.url}")
    private String activitiesUrl;

    @Value("${api.activities.key}")
    private String workoutsKey;

    @Value("${api.activities.host}")
    private String workoutsHost;

    @PostConstruct
    void fetchAllActivities() {
        activityRepository.deleteAll();
        List<Activity> allActivities = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {                            //1-9 intensity levels from api
            var workoutApiResponse = getActivityApiResponse(i);
            allActivities.addAll(parseWorkoutsFromApiResponse(workoutApiResponse));
        }
        activityRepository.saveAll(allActivities);
    }


    public List<Activity> searchActivity(String activity) {
        return activityRepository.findActivityByDescriptionContainingIgnoreCase(activity);
    }


    private ActivityApiResponse getActivityApiResponse(int intensityLevel) {
        ResponseEntity<ActivityApiResponse> responseEntity =
                restTemplate.exchange(activityUrlBuilder(intensityLevel), ActivityApiResponse.class);
        return responseEntity.getBody();
    }

    private List<Activity> parseWorkoutsFromApiResponse(ActivityApiResponse activityApiResponse) {
        if (activityApiResponse.getStatus_code() != 200) {
            throw new RuntimeException();
        }
        return activityApiResponse.getData();
    }

    private RequestEntity<Void> activityUrlBuilder(int intensitylevel) {
        URI uri = UriComponentsBuilder.fromUriString(activitiesUrl)
                .queryParam("intensitylevel", intensitylevel)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", workoutsKey);
        headers.set("X-RapidAPI-Host", workoutsHost);

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }



}
