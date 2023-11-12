package app.diary;

import app.diary.dto.DiaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface DiaryMapper {

    @Mapping(target = "productsInDiary")
    DiaryDto mapDiaryToDiaryDto(Diary diary);
}
