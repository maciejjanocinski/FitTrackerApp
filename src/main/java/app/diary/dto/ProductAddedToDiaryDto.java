package app.diary.dto;

import lombok.Builder;

@Builder
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
