package app.goal;

import app.bodyMetrics.Gender;
import app.diary.Diary;
import app.nutrients.Nutrients;
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
import java.util.Objects;

import static app.goal.GoalMapper.mapToGoalResponseDto;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final UserService userService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.activities.key}")
    private String workoutsKey;

    @Value("${api.activities.host}")
    private String workoutsHost;

    @Value("${api.macro-calculator.url}")
    private String url;


    GoalResponseDto getGoal(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();
        return mapToGoalResponseDto(diary);
    }

    @Transactional
    public GoalResponseDto setCustomGoal(Authentication authentication, AddCustomGoalDto addCustomGoalDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary().setCustomGoal(addCustomGoalDto, user.getBodyMetrics().getGender());

        return mapToGoalResponseDto(diary);
    }

    @Transactional
    public GoalResponseDto setGoal(Authentication authentication, AddGoalDto addGoalDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Gender gender = user.getBodyMetrics().getGender();
        Double height = user.getBodyMetrics().getHeight();
        Double weight = user.getBodyMetrics().getWeight();
        int age = user.getYears();
        BigDecimal fiber = Objects.equals(gender, Gender.MALE) ? BigDecimal.valueOf(38) : BigDecimal.valueOf(25);

        MacroCalculatorApiResponse response = GetGoalFromApi(addGoalDto, age, gender, height, weight);
        Nutrients nutrients = parseDataFromApiResponse(response, addGoalDto, fiber);
        Diary res = diary.setGoal(nutrients);

        return mapToGoalResponseDto(res);
    }

    private MacroCalculatorApiResponse GetGoalFromApi(AddGoalDto addGoalDto, int age, Gender gender, Double height, Double weight) {
        ResponseEntity<MacroCalculatorApiResponse> responseEntity =
                restTemplate.exchange(goalApiUriBuilder(addGoalDto, age ,gender, height, weight), MacroCalculatorApiResponse.class);
        return responseEntity.getBody();
    }

    private Nutrients parseDataFromApiResponse(MacroCalculatorApiResponse response, AddGoalDto addGoalDto, BigDecimal fiber) {
        ApiNutrition apiNutrition = switch (addGoalDto.dietType()) {
            case "balanced" -> response.getData().getBalanced();
            case "lowfat" -> response.getData().getLowfat();
            case "lowcarbs" -> response.getData().getLowcarbs();
            case "highprotein" -> response.getData().getHighprotein();
            default -> throw new IllegalStateException("Unexpected value: " + addGoalDto.goal());
        };

        return Nutrients.builder()
                .kcal(BigDecimal.valueOf(response.getData().getCalorie()))
                .proteinQuantityInGrams(BigDecimal.valueOf(apiNutrition.getProtein()))
                .carbohydratesQuantityInGrams(BigDecimal.valueOf(apiNutrition.getCarbs()))
                .fatQuantityInGrams(BigDecimal.valueOf(apiNutrition.getFat()))
                .fiberQuantityInGrams(fiber)
                .build();
    }

    private RequestEntity<Void> goalApiUriBuilder(AddGoalDto addGoalDto, int age, Gender gender, Double height, Double weight) {
        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("age", age)
                .queryParam("gender", gender.toString().toLowerCase())
                .queryParam("height", height)
                .queryParam("weight", weight)
                .queryParam("activitylevel", addGoalDto.activityLevel())
                .queryParam("goal", addGoalDto.goal())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", workoutsKey);
        headers.set("X-RapidAPI-Host", workoutsHost);

        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }


}
