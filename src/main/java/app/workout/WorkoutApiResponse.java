package app.workout;

import lombok.Data;
import java.util.List;

@Data
public class WorkoutApiResponse {
    private int status_code;
    private String request_result;
    private List<Activity> data;
}
