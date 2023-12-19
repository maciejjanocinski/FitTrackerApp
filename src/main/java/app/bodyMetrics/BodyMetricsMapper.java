package app.bodyMetrics;

import org.mapstruct.Mapper;

import static app.bodyMetrics.BodyMetrics.setGenderFromString;

@Mapper(componentModel = "spring")
public interface BodyMetricsMapper {

    static BodyMetricsDto mapBodyMetricsToBodyMetricsDto(BodyMetrics source) {
        return BodyMetricsDto.builder()
                .gender(source.getGender())
                .birthDate(source.getBirthDate())
                .height(source.getHeight())
                .weight(source.getWeight())
                .neck(source.getNeck())
                .waist(source.getWaist())
                .hip(source.getHip())
                .build();
    }

    static void updateBodyMetrics(BodyMetrics target, AddBodyMetricsDto dto){
        target.setGender(setGenderFromString(dto.gender().toString()));
        target.setBirthDate(dto.birthDate());
        target.setHeight(dto.height());
        target.setWeight(dto.weight());
        target.setNeck(dto.neck());
        target.setWaist(dto.waist());
        target.setHip(dto.hip());
    }


}
