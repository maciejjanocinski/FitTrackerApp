package app.diary.dto;


import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record EditProductInDiaryDto(long id,
                                    String measureLabel,
                                    BigDecimal quantity) {
}
