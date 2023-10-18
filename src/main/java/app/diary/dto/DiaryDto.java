package app.diary.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
@Builder
public record DiaryDto(
        BigDecimal sumKcal,
        BigDecimal sumProtein,
        BigDecimal sumCarbohydrates,
        BigDecimal sumFat,
        BigDecimal sumFiber,

        BigDecimal goalKcal,
        BigDecimal goalProtein,
        BigDecimal goalFat,
        BigDecimal goalCarbohydrates,
        BigDecimal goalFiber,
        BigDecimal leftKcal,
        BigDecimal leftProtein,
        BigDecimal leftFat,
        BigDecimal leftCarbohydrates,
        BigDecimal leftFiber,
        List<ProductInDiaryDto> products) {
}
