package app.user;

import app.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
interface UserMapper {

    UserDto mapUserToUserDto(User source);
}
