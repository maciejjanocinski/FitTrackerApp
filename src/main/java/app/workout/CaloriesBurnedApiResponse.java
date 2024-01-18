package app.workout;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
 class CaloriesBurnedApiResponse {
    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("request_result")
    private String requestResult;

    private KcalBurned data;
}

@Data
class KcalBurned {
    Double burnedCalorie;
    String unit;
}