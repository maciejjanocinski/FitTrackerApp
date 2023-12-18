package app.workout;

import lombok.Data;

@Data
public class CaloriesBurnedApiResponse {
    private int status_code;
    private String request_result;
    private KcalBurned data;
}

@Data
class KcalBurned {
    Double burnedCalorie;
    String unit;
}