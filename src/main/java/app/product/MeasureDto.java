package app.product;

import java.math.BigDecimal;

public record MeasureDto(
        String label,
        BigDecimal weight
) {
}
