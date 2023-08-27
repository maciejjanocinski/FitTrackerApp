package app.diary.dto;


public record EditProductInDiaryDto(long id,
                                    String measureLabel,
                                    double quantity) {
}
