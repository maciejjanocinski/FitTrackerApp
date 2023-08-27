package app.diary.dto;

public record AddProductToDiaryDto(String foodId,
                                   String name,
                                   String measureLabel,
                                   Double quantity) {
}
