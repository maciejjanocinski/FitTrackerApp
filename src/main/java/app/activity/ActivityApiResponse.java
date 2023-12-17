package app.activity;

import lombok.Data;
import java.util.List;

@Data
public class ActivityApiResponse {
    private int status_code;
    private String request_result;
    private List<Activity> data;
}
