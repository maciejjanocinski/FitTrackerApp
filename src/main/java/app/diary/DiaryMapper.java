package app.diary;

import app.diary.dto.DiaryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface DiaryMapper {

    DiaryDto mapDiaryToDiaryDto(Diary diary);
}
