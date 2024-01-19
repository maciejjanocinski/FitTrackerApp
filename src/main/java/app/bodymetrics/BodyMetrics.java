package app.bodymetrics;

import app.common.Gender;
import app.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static app.util.Utils.*;

@Entity
@Data
public class BodyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Gender gender;
    private LocalDate birthDate;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal neck;
    private BigDecimal waist;
    private BigDecimal hip;

    @OneToOne
    private User user;


    public BodyMetrics() {
        gender = Gender.MALE;
        birthDate = DEFAULT_BIRTH_DATE;
        height = DEFAULT_HEIGHT;
        weight = DEFAULT_WEIGHT;
        neck = DEFAULT_NECK_SIZE;
        waist = DEFAULT_WAIST_SIZE;
        hip = DEFAULT_HIP_SIZE;
    }

    void updateBodyMetrics(AddBodyMetricsDto dto) {
        this.setGender(dto.gender());
        this.setBirthDate(dto.birthDate());
        this.setHeight(dto.height());
        this.setWeight(dto.weight());
        this.setNeck(dto.neck());
        this.setWaist(dto.waist());
        this.setHip(dto.hip());
    }
}
