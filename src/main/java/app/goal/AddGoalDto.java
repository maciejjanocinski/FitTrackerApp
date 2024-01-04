package app.goal;

import lombok.Builder;

@Builder
public record AddGoalDto(
        int activityLevel,
        String goal,
        String dietType
) {

}
