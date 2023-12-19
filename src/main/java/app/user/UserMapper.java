package app.user;

import app.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static app.bodyMetrics.BodyMetricsMapper.mapBodyMetricsToBodyMetricsDto;

@Mapper(componentModel = "spring")
interface UserMapper {

   static UserDto mapUserToUserDto(User source) {
        return UserDto.builder()
                .username(source.getUsername())
                .name(source.getName())
                .surname(source.getSurname())
                .email(source.getEmail())
                .phone(source.getPhone())
                .bodyMetrics(mapBodyMetricsToBodyMetricsDto(source.getBodyMetrics()))
                .build();
   };
}
