package app.diary;

import app.diary.dto.DiaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static app.product.ProductMapper.mapToProductDtoList;

@Mapper(componentModel = "spring")
interface DiaryMapper {

    static DiaryDto mapDiaryToDiaryDto(Diary diary) {
        return DiaryDto.builder()
                .sumKcal(diary.getNutrientsSum().getKcal())
                .sumProtein(diary.getNutrientsSum().getProteinQuantityInGrams())
                .sumCarbohydrates(diary.getNutrientsSum().getCarbohydratesQuantityInGrams())
                .sumFat(diary.getNutrientsSum().getFatQuantityInGrams())
                .sumFiber(diary.getNutrientsSum().getFiberQuantityInGrams())
                .goalKcal(diary.getGoalNutrients().getKcal())
                .goalProtein(diary.getGoalNutrients().getProteinQuantityInGrams())
                .goalFat(diary.getGoalNutrients().getFatQuantityInGrams())
                .goalCarbohydrates(diary.getGoalNutrients().getCarbohydratesQuantityInGrams())
                .goalFiber(diary.getGoalNutrients().getFiberQuantityInGrams())
                .leftKcal(diary.getNutrientsLeft().getKcal())
                .leftProtein(diary.getNutrientsLeft().getProteinQuantityInGrams())
                .leftFat(diary.getNutrientsLeft().getFatQuantityInGrams())
                .leftCarbohydrates(diary.getNutrientsLeft().getCarbohydratesQuantityInGrams())
                .leftFiber(diary.getNutrientsLeft().getFiberQuantityInGrams())
                .productsInDiary(mapToProductDtoList(diary.getProducts()))
                .build();
    }
}
