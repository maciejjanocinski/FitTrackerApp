package app.product;

import app.nutrients.Nutrients;
import app.nutrients.NutrientsDto;
import lombok.Builder;
import java.util.List;
import java.math.BigDecimal;
@Builder
public record ProductDto(
        Long id,
        String name,
        NutrientsDto nutrients,
        String currentlyUsedMeasureName,
        BigDecimal quantity,
        String image,
        String query,
        List<MeasureDto> measures
) {}
