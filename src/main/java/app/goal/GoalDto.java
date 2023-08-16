package app.goal;

import lombok.AllArgsConstructor;
import lombok.Data;


record GoalDto(double kcal,
               double proteinPercentage,
               double carbohydratesPercentage,
               double fatPercentage) {
}
