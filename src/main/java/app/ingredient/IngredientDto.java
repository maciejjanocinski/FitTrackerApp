package app.ingredient;

import app.nutrients.NutrientsDto;
import lombok.Builder;
import java.util.List;
import java.math.BigDecimal;
@Builder
public record IngredientDto(
        Long id,
        String name,
        NutrientsDto nutrients,
        String currentlyUsedMeasureName,
        BigDecimal quantity,
        String image,
        String query,
        List<MeasureDto> measures
) {}
