package app.util;

import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper( ProductMapper.class );

    @Mapping(source = "numberOfSeats", target = "seatCount")
    Prodi carToCarDto(Car car);
}
