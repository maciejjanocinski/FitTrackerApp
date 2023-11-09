package app.diary.dto;


import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record EditProductInDiaryDto(Long id,
                                    String measureLabel,
                                    BigDecimal quantity) {
}
