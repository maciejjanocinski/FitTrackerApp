package app.diary.dto;

public record ProductAddedToDiaryDto(String productId,
                                     String productName,
                                     double kcal,
                                     double protein,
                                     double carbohydrates,
                                     double fat,
                                     double fiber,
                                     String image,
                                     String measureLabel,
                                     double quantity) {
}
