package app.goal;

import lombok.Data;
@Data
public class MacroCalculatorApiResponse {
    private int status_code;
    private String request_result;
    private ApiData data;
}

@Data
class ApiData {
    private Double calorie;
    private ApiNutrition balanced;
    private ApiNutrition lowfat;
    private ApiNutrition lowcarbs;
    private ApiNutrition highprotein;
}

@Data
class ApiNutrition {
    Double protein;
    Double fat;
    Double carbs;
}