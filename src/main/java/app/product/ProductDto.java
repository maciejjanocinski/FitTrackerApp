package app.product;

import java.math.BigDecimal;
import java.util.List;
public record ProductDto(
        Long id,
        String productId,
        String name,
        BigDecimal kcal,
        BigDecimal protein,
        BigDecimal fat,
        BigDecimal carbohydrates,
        BigDecimal fiber,
        String image,
        boolean isUsed,
        String query,
        List<Measure> measures
) {}
