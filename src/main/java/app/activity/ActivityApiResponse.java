package app.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
class ActivityApiResponse {
    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("request_result")
    private String requestResult;

    private List<Activity> data;
}
