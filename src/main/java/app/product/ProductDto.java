package app.product;

import lombok.Builder;
import java.util.List;
import java.math.BigDecimal;
@Builder
public record ProductDto(
        Long id,
        String name,
        BigDecimal kcal,
        BigDecimal protein,
        BigDecimal fat,
        BigDecimal carbohydrates,
        BigDecimal fiber,
        String image,
        BigDecimal quantity,
        String currentlyUsedMeasure,
        boolean isUsed,
        String query,
        List<Measure> measures
) {}
