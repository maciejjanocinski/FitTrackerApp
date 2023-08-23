package app.diary;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DiaryMapper {

    DiaryMapper INSTANCE = Mappers.getMapper(DiaryMapper.class);

    DiaryDto mapDiaryToDiaryDto(Diary diary);
}
