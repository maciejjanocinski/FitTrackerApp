package app.user;

import app.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto mapToDto(User source);
}
