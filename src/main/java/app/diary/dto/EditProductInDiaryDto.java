package app.diary.dto;


import java.math.BigDecimal;

public record EditProductInDiaryDto(long id,
                                    String measureLabel,
                                    BigDecimal quantity) {
}
