package app.dto;

import lombok.Data;

@Data
public record GoalsDto(double kcal,
                       double proteinPercentage,
                       double carbohydratesPercentage,
                       double fatPercentage) {


}
