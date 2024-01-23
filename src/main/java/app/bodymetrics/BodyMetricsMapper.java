package app.bodymetrics;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BodyMetricsMapper {

     BodyMetricsMapper INSTANCE = Mappers.getMapper(BodyMetricsMapper.class);
     BodyMetricsDto mapToDto(BodyMetrics source);




}
