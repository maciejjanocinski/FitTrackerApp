package app.diary.dto;


import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record EditProductInDiaryDto(Long id,
                                    String measureLabel,
                                    @Positive(message = "Quantity must be positive.")
                                    BigDecimal quantity
) {
}
