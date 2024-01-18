package app.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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

import static app.util.Utils.INTENSITY_LEVEL_MAX;
import static app.util.Utils.INTENSITY_LEVEL_MIN;

@Service
@RequiredArgsConstructor
 class ActivityService implements CommandLineRunner{

    private final ActivityRepository activityRepository;

    @Value("${api.activities.url}")
    private String activitiesUrl;

    @Value("${api.activities.key}")
    private String workoutsKey;

    @Value("${api.activities.host}")
    private String workoutsHost;

    @Override
    public void run(String... args){
        fetchAllActivities();
    }

   void fetchAllActivities() {
        activityRepository.deleteAll();
        List<Activity> allActivities = new ArrayList<>();
        for (int i = INTENSITY_LEVEL_MIN; i <= INTENSITY_LEVEL_MAX; i++) {
            ActivityApiResponse workoutApiResponse = getActivityApiResponse(i);
            allActivities.addAll(parseWorkoutsFromApiResponse(workoutApiResponse));
        }
        activityRepository.saveAll(allActivities);
    }

     List<Activity> searchActivity(String activity) {
        return activityRepository.findActivityByDescriptionContainingIgnoreCase(activity);
    }

    private ActivityApiResponse getActivityApiResponse(int intensityLevel) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ActivityApiResponse> responseEntity =
                restTemplate.exchange(activityUrlBuilder(intensityLevel), ActivityApiResponse.class);
        return responseEntity.getBody();
    }

    private List<Activity> parseWorkoutsFromApiResponse(ActivityApiResponse activityApiResponse) {
        if (activityApiResponse.getStatusCode() != 200) {
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
