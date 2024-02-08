package app.ingredient;

import java.math.BigDecimal;

public record MeasureDto(
        String label,
        BigDecimal weight
) {
}
