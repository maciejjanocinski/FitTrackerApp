package app.diary.dto;

import java.util.List;

public record DiaryDto(
        double sumKcal,
        double sumProtein,
        double sumCarbohydrates,
        double sumFat,
        double sumFiber,

        double goalKcal,
        double goalProtein,
        double goalFat,
        double goalCarbohydrates,
        double goalFiber,
        double leftKcal,
        double leftProtein,
        double leftFat,
        double leftCarbohydrates,
        double leftFiber,
        List<ProductAddedToDiaryDto> products) {
}
