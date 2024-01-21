package app.diary;

import app.diary.dto.DiaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
interface DiaryMapper {
    DiaryMapper INSTANCE = Mappers.getMapper(DiaryMapper.class);

    DiaryDto mapToDto(Diary diary);

}
