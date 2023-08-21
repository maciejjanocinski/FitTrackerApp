package app.user;

import app.diary.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface UserMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    UserDto mapToProductAddedToDiary(User source);
}
